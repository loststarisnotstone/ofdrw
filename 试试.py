import jpype

# 启动 JVM，设置 classpath 指向 ofdrw.jar 的路径
jpype.startJVM(jpype.getDefaultJVMPath(), "-Djava.class.path=C:/Users/liugu/Documents/GitHub/ofdrw/ofdrw.jar")


# 导入需要的 Java 类
from org.ofdrw.core import OFDDocument

# 示例：创建 OFDDocument 实例
ofd_doc = OFDDocument()
# 这里可以使用其他方法进行处理

# 关闭 JVM
jpype.shutdownJVM()
