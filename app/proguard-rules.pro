# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\Android\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
-ignorewarnings
# �������ѹ���ȣ���0~7֮�䣬Ĭ��Ϊ5��һ�㲻���޸�
-optimizationpasses 5

# ���ʱ��ʹ�ô�Сд��ϣ���Ϻ������ΪСд
-dontusemixedcaseclassnames

# ָ����ȥ���Էǹ��������
-dontskipnonpubliclibraryclasses

# ��仰�ܹ�ʹ���ǵ���Ŀ���������ӳ���ļ�
# ����������->������������ӳ���ϵ
-verbose

# ָ����ȥ���Էǹ���������Ա
-dontskipnonpubliclibraryclassmembers

# ����ԤУ�飬preverify��proguard���ĸ�����֮һ��Android����Ҫpreverify��ȥ����һ���ܹ��ӿ�����ٶȡ�
-dontpreverify

# ����Annotation������
-keepattributes *Annotation*,InnerClasses

# �����������
-keepattributes Signature

# �׳��쳣ʱ���������к�
-keepattributes SourceFile,LineNumberTable

# ָ�������ǲ��õ��㷨������Ĳ�����һ��������
# ����������ǹȸ��Ƽ����㷨��һ�㲻������
-optimizations !code/simplification/cast,!field/*,!class/merging/*

