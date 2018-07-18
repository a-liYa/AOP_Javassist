package com.aliya.plugin

import com.aliya.plugin.compiling.AppConfigGenerator
import com.aliya.plugin.extension.ConfigBean
import com.android.build.gradle.AppPlugin
import com.android.build.gradle.LibraryPlugin
import com.android.build.gradle.internal.scope.VariantScope
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
                // 获取到scope,作用域
                BaseVariantData variantData = variant.variantData
                VariantScope scope = variantData.scope

                // 创建一个task
                def taskName = scope.getTaskName("appConfig", "Test")
                def createTask = project.task(taskName)

                // 设置task要执行的任务
                createTask.doLast {
                    createAppConfigJava(variant, project.appConfig)
                }

                def generateBuildConfigTask = scope.generateBuildConfigTask
                if (generateBuildConfigTask) {
                    createTask.dependsOn generateBuildConfigTask
                    generateBuildConfigTask.finalizedBy createTask
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

//        // 要生成的内容
//        def content = """package com.aliya.config;
//
//                        public final class AppConfig {
//                            public static final String API_BASE = "${config.apiBase}";
//                        }
//                        """
//        // 获取到BuildConfig类的路径
//        File outputDir = variant.variantData.scope.buildConfigSourceOutputDir
//        logger.error("dir " + outputDir.path)
//        File javaFile = new File(outputDir, "AppConfig.java")
//
//        javaFile.write(content, 'UTF-8');

    }

}