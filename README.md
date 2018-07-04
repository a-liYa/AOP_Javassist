# AOP_Javassist

## Javassist

> Javassist 是一个执行字节码操作的库。它可以在一个已经编译好的类中添加新的方法，或者是修改已有的方法，并且不需要对字节码方面有深入的了解。

## Gradle

>Javassist 可以绕过编译，直接操作字节码，从而实现代码注入，所以使用 Javassist 的时机就是在构建工具 Gradle 将源文件编译成 .class 文件之后，在将 .class 打包成 dex 文件之前。