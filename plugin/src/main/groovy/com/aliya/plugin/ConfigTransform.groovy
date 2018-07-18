package com.aliya.plugin

import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformException
import com.android.build.api.transform.TransformInvocation
import com.android.build.gradle.internal.pipeline.TransformManager
import org.gradle.api.Project

class ConfigTransform extends Transform {

    Project project

    ConfigTransform(Project project) {
        this.project = project
    }

    @Override
    String getName() {
        return "AppConfig"
    }

    /**
     * 指定处理的数据类型，仅两种枚举类型，CLASSES代表处理的java的class文件，RESOURCES代表要处理java的资源
     *
     * @return {@link TransformManager#CONTENT_CLASS} {@link TransformManager#CONTENT_RESOURCES}
     */
    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    @Override
    boolean isIncremental() {
        return false
    }

    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {

    }

}