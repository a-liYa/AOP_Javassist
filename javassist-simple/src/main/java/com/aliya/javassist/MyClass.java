package com.aliya.javassist;

import java.io.IOException;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.NotFoundException;

public class MyClass {

    static String sPath = MyClass.class.getClassLoader().getResource("").getFile();

    public static void main(String[] args) {

        System.out.println("main a_liYa start");

        insetMethodBody();

        System.out.println("main a_liYa end");

    }

    private static void insetMethodBody() {
        try {
            ClassPool pool = ClassPool.getDefault();
            CtClass cc = pool.get("com.aliya.javassist.Hello");
            CtMethod m = cc.getDeclaredMethod("say");
            m.insertBefore("{ System.out.println(\"Hello.say():\"); }");
            Class c = cc.toClass();
            cc.writeFile(sPath);
            Hello h = (Hello) c.newInstance();
            h.say();

        } catch (NotFoundException e) {
            e.printStackTrace();
        } catch (CannotCompileException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void createNewClass() {


        try {
//            ClassPool.doPruning = true;
            ClassPool pool = ClassPool.getDefault();
            CtClass cc = pool.get("com.aliya.javassist.MyClass");
            cc.setSuperclass(pool.get("com.aliya.javassist.SuperClass"));
            cc.writeFile(sPath);

            // 定义新类

            CtClass point = pool.makeClass("com.aliya.Point");
            point.addMethod(CtNewMethod.abstractMethod(pool.get("java.lang.Void"), "test", null,
                    null, point));
            point.writeFile(sPath);

            CtClass ctClass = pool.makeInterface("com.aliya.IPoint");
            ctClass.debugWriteFile();
//            System.out.println("" + cc.toClass().getSuperclass());

        } catch (NotFoundException e) {
            e.printStackTrace();
        } catch (CannotCompileException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
