package com.sduoj.judgeserver.util;

import com.sduoj.judgeserver.exception.internal.ProcessException;
import com.sduoj.judgeserver.util.os.OSUtil;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.nio.file.Path;


/**
 * @Author: Song yang Ji
 * @ProjectName: sduoj-judge-server
 * @Version 1.0
 * @Description: 文件管理工具类
 *
 */


@Component
public class FileUtil {

    @Resource
    OSUtil osUtil;


    /**
     * 删除一个目录及其内部的子目录或文件
     * @param path 需要删除的文件路径
     * @return 成功删除与否
     * 调用下面的私有的remove方法进行递归删除
     */

    public boolean removeFileOrDirectory(Path path) {
        File file = path.toFile();
        return remove(file);
    }

    private boolean remove(File file) {
        if (file == null) return false;
        if (file.isFile()) {
            return file.delete();
        }
        boolean res = true;
        File[] files = file.listFiles();
        if (files == null) {
            return file.delete();
        }
        // 先删文件
        for (File f : files) {
            boolean temp = remove(f);
            res = res && temp;
        }
        // 后删目录
        boolean temp = file.delete();
        res = res && temp;
        return res;
    }

    public void removeFileForce(Path path) throws ProcessException {
        osUtil.execCommandBySuperUser("rm "+path.toAbsolutePath().toString());
    }


    /**
     * 将文件或者目录的写权限开启（对anyone 都有读、写、执行权限）
     * @param path 文件或者文件夹路径
     * @throws ProcessException 自定义进程执行异常（因为需要调用操作系统的命令执行）
     */
    public void openPermissions(Path path) throws ProcessException {
        osUtil.execCommandBySuperUser("chmod 777 " + path.toAbsolutePath());
    }

    public void openPermissionsRecursively(Path path) throws ProcessException {
        osUtil.execCommandBySuperUser("chmod -R 777 " + path.toAbsolutePath());
    }


    /**
     * 将文件夹或者文件写权限关闭（对 other 而言，只有读、执行权限）
     * @param path 文件或者文件夹路径
     * @throws ProcessException 自定义进程执行异常
     */
    public void offPermissions(Path path) throws ProcessException {
        osUtil.execCommandBySuperUser("chmod 775 " + path.toAbsolutePath());
    }

    public void offPermissionsRecursively(Path path) throws ProcessException {
        osUtil.execCommandBySuperUser("chmod -R 775 " + path.toAbsolutePath());
    }

}