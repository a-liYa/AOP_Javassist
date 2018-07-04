package com.aliya.plugin;

import com.android.build.api.transform.JarInput;
import com.android.build.api.transform.QualifiedContent;
import com.android.build.api.transform.Transform;
import com.android.build.api.transform.TransformException;
import com.android.build.api.transform.TransformInput;
import com.android.build.api.transform.TransformInvocation;
import com.google.common.collect.Sets;

import org.gradle.api.Project;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

/**
 * 自定义 Transform
 *
 * @author a_liYa
 * @date 2018/7/3 17:55.
 */
public class JavassistTransform extends Transform {

    Project mProject;

    public JavassistTransform(Project project) {
        this.mProject = project;
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
     * 指定输入的类型，通过这里的设定，可以指定我们要处理的文件类型这样确保其他类型的文件不会传入
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
        if (mProject.getPlugins().hasPlugin("com.android.application")) {
            return Sets.immutableEnumSet(
                    QualifiedContent.Scope.PROJECT,
                    QualifiedContent.Scope.SUB_PROJECTS,
                    QualifiedContent.Scope.EXTERNAL_LIBRARIES);
        } else if (mProject.getPlugins().hasPlugin("com.android.library")) {
            return Sets.immutableEnumSet(QualifiedContent.Scope.PROJECT);
        } else {
            return Collections.emptySet();
        }
    }

    @Override
    public boolean isIncremental() {
        return false;
    }

    @Override
    public void transform(TransformInvocation transformInvocation) throws TransformException,
            InterruptedException, IOException {

        Collection<TransformInput> inputs = transformInvocation.getInputs();
        // Transform的inputs有两种类型，一种是目录，一种是jar包，要分开遍历
        for (TransformInput input : inputs) {
            Collection<JarInput> jarInputs = input.getJarInputs();
            // 对 jar 包类型的 inputs 进行遍历
            for (JarInput jarInput : jarInputs) {

            }
        }

        super.transform(transformInvocation);
    }

}
