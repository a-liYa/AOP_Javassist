package com.aliya.plugin

import com.aliya.plugin.compiling.AppConfigGenerator
import com.aliya.plugin.extension.ConfigBean
import com.android.build.gradle.AppPlugin
import com.android.build.gradle.LibraryPlugin
import com.android.build.gradle.internal.api.ApplicationVariantImpl
import com.android.build.gradle.internal.variant.BaseVariantData
import org.gradle.api.Plugin
import org.gradle.api.Project

class AppConfigPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {

        if (project.plugins.hasPlugin(AppPlugin)
                || project.plugins.hasPlugin(LibraryPlugin)) {

            def android = project.android
            // 注册一个Transform
            android.registerTransform(new ConfigTransform(project))

            project.extensions.create("appConfig", ConfigBean)

            android.applicationVariants.all { variant ->
                if (variant instanceof ApplicationVariantImpl) {
                    // 获取 变体数据
                    BaseVariantData variantData = variant.variantData

                    // 创建一个task
                    def taskName = variantData.getTaskName("appConfig", "Test")
                    def createTask = project.task(taskName)

                    // 设置task要执行的任务
                    createTask.doLast {
                        createAppConfigJava(variant, project.appConfig)
                    }

                    def generateBuildConfigTask = variant.generateBuildConfig
                    if (generateBuildConfigTask) {
                        createTask.dependsOn generateBuildConfigTask
                        generateBuildConfigTask.finalizedBy createTask
                    }
                }
            }

        }

    }

    static void createAppConfigJava(variant, config) {

        AppConfigGenerator generator = new AppConfigGenerator(
                variant.variantData.scope.buildConfigSourceOutputDir,
                variant.variantData.variantConfiguration.originalApplicationId)

        generator.addField("String", "API_BASE", '"' + config.apiBase + '"')

        generator.generate()

    }

}