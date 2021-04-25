package com.perfma.xlab.change.var;

import com.perfma.xlab.change.var.feature.CommandInfo;
import com.perfma.xlab.change.var.feature.NativeInit;
import com.perfma.xlab.change.var.feature.PlatformInfo;
import com.perfma.xlab.xpocket.spi.AbstractXPocketPlugin;
import com.perfma.xlab.xpocket.spi.context.SessionContext;
import com.perfma.xlab.xpocket.spi.process.XPocketProcess;

import static com.perfma.xlab.change.var.feature.JniFunctionWrapper.*;

/**
 * @author: ZQF
 * @date: 2021-04-23
 * @description: desc
 */
public class ChangeVarPlugin extends AbstractXPocketPlugin implements Runnable {

    private boolean attachStatus = false;

    private XPocketProcess process;

    private int pid = -1;

    private SessionContext context;

    private CommandInfo commandInfo;

    private volatile boolean done;
    private volatile boolean exit;

    @Override
    public void destory() throws Throwable {
        exit = true;
        detach0();
        attachOff();
    }


    @Override
    public void init(XPocketProcess process) {
        NativeInit.nativeInit();

    }

    @Override
    public void switchOn(SessionContext context) {
        this.context = context;
        context.setPid(pid);
    }

    public String details(String cmd) {
        if("attach".equals(cmd)) {
            return "DESCRIPTION : \n     attach [pid] ";
        }

        return null;
    }

    public boolean isAvailableNow(String cmd) {
        if(attachStatus) {
            return !"attach".equals(cmd);
        } else {
            return "attach".equals(cmd);
        }
    }

    private void attachOff() {
        if(context != null) {
            attachStatus = false;
            pid = -1;
            context.setPid(pid);
        }
    }

    public void invoke(XPocketProcess process) throws Throwable {
        if(!PlatformInfo.isSupport()){
            process.output("not support os or cpu arch, current: " + PlatformInfo.getOS() + "," + PlatformInfo.getCPU() + "," + PlatformInfo.getBit());
            process.end();
            return ;
        }
        this.process = process;
        if("attach".equals(process.getCmd())){
            exit = false;
            new Thread(this).start();
        }
        commandInfo = new CommandInfo(process.getCmd(), process.getArgs());
        done = false;
        while(!done){
        }
    }

    private void deal() throws Throwable{
        if(this.commandInfo != null) {
            CommandInfo commandInfo = this.commandInfo;
            String cmd = commandInfo.getCmd();
            String[] args = commandInfo.getArgs();

            switch (cmd) {
                case "attach":
                    if(args == null || args.length == 0){
                        process.output("[attach pid]");
                        process.end();
                        return ;
                    }
                    attach(args[0]);
                    break;
                case "detach":
                    destory();
                    process.end();
                    break;
                case "read":
                    read(args);
                    break;
                case "write":
                    write(args);
                    break;
                default:
                    process.end();

            }
            done = true;
            commandInfo = null;
        }
    }

    private void read(String[] args){
        if(!checkParam(args, 2)){
            return ;
        }
        long addr = getSymbolAddr(args[0]);
        if(isValid(args, 2, addr)){


            int length = Integer.parseInt(args[1]);
            byte[] bytes = new byte[length];
            int err = readAddrValue(addr, bytes, length);
            if(err != 0){
                process.output("read value error " + err);
                process.end();
                return ;
            }
            process.output(args[0] + " value: " + toValue(bytes));
            process.end();
        }
    }

    private long toValue(byte[] bytes){
        int length = bytes.length;
        long res = 0;
        for(int i = length-1; i >= 0; i --){
            res <<= 8;
            res |= bytes[i];
        }
        return res;
    }

    private byte[] toByte(long value, int length){
        byte[] res = new byte[length];
        long tmp = value;
        for(int i = 0; i < length; i ++){
            res[i] = (byte) (tmp % (1 << 8));
            tmp >>= 8;
        }
        if(res[length-1] == 0 && value < 0){
            res[length-1] = (byte) 0xF0;
        }
        return res;
    }

    private void write(String[] args){
        if(!checkParam(args, 3)){
            return ;
        }
        long addr = getSymbolAddr(args[0]);
        if(isValid(args, 3, addr)){
            int length = Integer.parseInt(args[1]);
            byte[] bytes = toByte(Long.parseLong(args[2]), length);
            int err = writeAddrValue(addr, bytes, length);
            if(err != 0){
                process.output("write value error " + err);
                process.end();
                return ;
            }
            process.output("write value success");
            process.end();
        }
    }

    private boolean checkParam(String[] args, int length){

        if(args == null || args.length != length){
            process.output("args input error");
            process.end();
            return false;
        }
        return true;
    }

    private boolean isValid(String[] args, int length, long addr){

        if(addr <= 0){
            process.output("Symbol " + args[0] + " can't find");
            process.end();
            return false;
        }
        int len;
        try {
             len = Integer.parseInt(args[1]);
        }catch (Exception e){
            process.output("length must is Integer");
            process.end();
            return false;
        }
        if(len != 1 && len != 4 && len != 8){
            process.output("length must is [1,4,8]");
            process.end();
            return false;
        }
        if(length == 3){
            boolean hasException = false;
            try{
                Long.parseLong(args[2]);
            }catch (Exception e){
                hasException = true;
            }
            if(hasException){
                process.output("value must be number");
                process.end();
                return false;
            }
        }
        return true;
    }

    private void attach(String arg){
        if (!attachStatus) {
            int pid = -1;
            try{
                pid = Integer.parseInt(arg);
            }catch (Exception ignore){

            }
            if(pid > 0) {
                int err = attach0(pid);
                if(err != 0){
                    process.output("attach " + arg + " fail" );
                }else{
                    attachStatus = true;
                    attachOn(pid);
                }
            }
        }
        process.end();
    }

    private void attachOn(int pid) {
        if(context != null) {
            attachStatus = true;
            this.pid = pid;
            context.setPid(pid);
        }
    }

    @Override
    public void run() {
        while(!exit){
            try {
                if (!done) {
                    deal();
                }
            }catch (Throwable e){
                e.printStackTrace();
                return ;
            }
        }
    }
}
