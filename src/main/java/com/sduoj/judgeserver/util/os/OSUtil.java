package com.sduoj.judgeserver.util.os;


import com.sduoj.judgeserver.conf.EnvironmentConfig;
import com.sduoj.judgeserver.exception.internal.ProcessException;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;


/**
 * @Author: Song yang Ji
 * @ProjectName: sduoj-judge-server
 * @Version 1.0
 * @Description: OS工具类
 * 它的工作较单一，接受需要执行的命令（字符串形式），然后去执行它
 *
 */

@Component
public class OSUtil {


    @Resource
    EnvironmentConfig environmentConfig;

    private File homeFile;

    private String shell;


    @PostConstruct
    private void init() {
        Path homePath = Paths.get(environmentConfig.getHome());
        homeFile = homePath.toFile();
        shell = environmentConfig.getShell();
    }


    public void execCommand(String command) throws ProcessException {
        ProcessBuilder processBuilder = new ProcessBuilder(shell, "-c", command).directory(homeFile);
        new ProcessWorker(processBuilder).work();
    }


    public void execCommandBySuperUser(String command) throws ProcessException {
        String cmd = getSudoCommand(command);
        ProcessBuilder processBuilder = new ProcessBuilder(shell, "-c", cmd).directory(homeFile);
        new ProcessWorker(processBuilder).work();
    }


    public List<String> execCommandWithResult(String command) throws ProcessException {
        return getStrings(command);
    }

    private List<String> getStrings(String command) throws ProcessException {
        return getStrings(command, homeFile.toPath());
    }

    private List<String> getStrings(String command, Path path) throws ProcessException {
        ProcessBuilder processBuilder = new ProcessBuilder(shell, "-c", command).directory(path.toFile());
        return new ProcessWorker(processBuilder).job();
    }


    public List<String> execCommandBySuperUserWithResult(String command) throws ProcessException {
        String cmd = getSudoCommand(command);
        return getStrings(cmd);
    }

    public List<String> execCommandBySuperUserWithResult(String command, Path path) throws ProcessException {
        String cmd = getSudoCommand(command);
        return getStrings(cmd, path);
    }




    public String getSudoCommand(String cmd) {
        // 如果不需要密码的话
        if (environmentConfig.getPassword() == null || environmentConfig.getPassword().length() == 0) {
            return "sudo " + cmd;
        }
        return "echo " + "'" + environmentConfig.getPassword() + "' | sudo -S " + cmd;
    }

}