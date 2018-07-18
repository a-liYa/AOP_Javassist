package com.aliya.plugin;

import com.aliya.plugin.javassist.JarClassPath;
import com.android.build.api.transform.DirectoryInput;
import com.android.build.api.transform.Format;
import com.android.build.api.transform.JarInput;
import com.android.build.api.transform.QualifiedContent;
import com.android.build.api.transform.Transform;
import com.android.build.api.transform.TransformException;
import com.android.build.api.transform.TransformInput;
import com.android.build.api.transform.TransformInvocation;
import com.android.build.gradle.AppExtension;
import com.google.common.collect.Sets;

import org.apache.commons.io.FileUtils;
import org.gradle.api.Project;
import org.gradle.api.logging.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javassist.ClassPath;
import javassist.ClassPool;
import javassist.NotFoundException;

/**
 * 自定义 Transform
 *
 * @author a_liYa
 * @date 2018/7/3 17:55.
 */
public class JavassistTransform extends Transform {

    Project project;
    AppExtension android;
    Logger logger;

    public JavassistTransform(Project project) {
        this.project = project;
        android = project.getExtensions().getByType(AppExtension.class);
        logger = project.getLogger();
    }

    /**
     * 设置我们自定义的Transform对应的Task名称
     *
     * @return 名称
     */
    @Override
    public String getName() {
        return "JavassistTrans";
    }

    /**
     * 指定输入的类型，可以指定我们要处理的文件类型这样确保其他类型的文件不会传入
     *
     * @return Set
     */
    @Override
    public Set<QualifiedContent.ContentType> getInputTypes() {
        return Collections.singleton(QualifiedContent.DefaultContentType.CLASSES);
    }

    /**
     * 指定Transform的作用范围
     *
     * @return Set
     */
    @Override
    public Set<? super QualifiedContent.Scope> getScopes() {
        if (project.getPlugins().hasPlugin("com.android.application")) {
            return Sets.immutableEnumSet(
                    QualifiedContent.Scope.PROJECT,
                    QualifiedContent.Scope.SUB_PROJECTS,
                    QualifiedContent.Scope.EXTERNAL_LIBRARIES);
        } else if (project.getPlugins().hasPlugin("com.android.library")) {
            return Sets.immutableEnumSet(QualifiedContent.Scope.PROJECT);
        } else {
            return Collections.emptySet();
        }
    }

    /**
     * 指明当前Transform是否支持增量编译
     * @return false:不支持
     */
    @Override
    public boolean isIncremental() {
        return false;
    }

    @Override
    public void transform(TransformInvocation transformInvocation) throws TransformException,
            InterruptedException, IOException {
        ClassPool pool = ClassPool.getDefault();
        try {
            pool.appendClassPath(android.getBootClasspath().get(0).getPath());
        } catch (NotFoundException e) {
            e.printStackTrace();
        }


        List<ClassPath> classPaths = new ArrayList<>();
        Collection<TransformInput> inputs = transformInvocation.getInputs();
        // Transform的inputs有两种类型，一种是目录，一种是jar包，要分开遍历
        for (TransformInput input : inputs) {

            // 对 jar 包类型的 inputs 进行遍历
            Collection<JarInput> jarInputs = input.getJarInputs();
            for (JarInput jarInput : jarInputs) {
                logger.error("jarInput.file = " + jarInput.getFile().getAbsolutePath());
                try {
                    ClassPath classpath = new JarClassPath(jarInput.getFile().getAbsolutePath());
                    classPaths.add(classpath);
                    pool.appendClassPath(classpath);

                    String jarName = jarInput.getName();
                    if (jarName.endsWith(".jar")) {
                        jarName = jarName.substring(0, jarName.length() - 4);
                    }
                    File destFile = transformInvocation.getOutputProvider()
                            .getContentLocation(jarName, jarInput.getContentTypes(), jarInput.getScopes(), Format.JAR);

                    // 将jarInput文件拷贝到output指定目录
                    FileUtils.copyFile(jarInput.getFile(), destFile);
                } catch (NotFoundException e) {
                    e.printStackTrace();
                }

            }

            // 遍历文件夹
            Collection<DirectoryInput> dirInputs = input.getDirectoryInputs();
            for (DirectoryInput dirInput : dirInputs) {
                logger.error("dirInput.file = " + dirInput.getFile().getPath());
                File destDir = transformInvocation.getOutputProvider()
                        .getContentLocation(dirInput.getName(), dirInput.getContentTypes(), dirInput.getScopes(), Format.DIRECTORY);
                logger.error("destDir = " + destDir.getPath());
                try {
                    pool.appendClassPath(dirInput.getFile().getAbsolutePath());
                } catch (NotFoundException e) {
                    e.printStackTrace();
                }

                if (dirInput.getChangedFiles() != null && !dirInput.getChangedFiles().isEmpty()) {
                    logger.error("changedFiles != null");
                } else {
//                    dirInput.getFile().
                    logger.error("changedFiles == null");
                }
            }


        }
    }

}
