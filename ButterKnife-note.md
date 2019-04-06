Compile is ok, but class not fund at runtime

Because the dependence added were follow Java, It's not right.

```
apply plugin: 'kotlin-android'
apply plugin: 'kotlin-android-extensions'
apply plugin: 'kotlin-kapt'
...
dependencies {
    // kotlin, implementation in java
    kapt deps.butterknife.compiler
    implementation deps.kotlin.stdlib
    implementation deps.butterknife.core
}
```
versions.gradle
```
def versions = [:]
versions.kotlin = "1.3.21"
versions.butterknife = "10.0.0"

def deps = [:]
deps.android_gradle_plugin = "com.android.tools.build:gradle:3.3.2"
deps.butterknife_gradle_plugin = "com.jakewharton:butterknife-gradle-plugin:9.0.0-rc2"

def kotlin = [:]
kotlin.stdlib = "org.jetbrains.kotlin:kotlin-stdlib-jdk7:$versions.kotlin"
kotlin.test = "org.jetbrains.kotlin:kotlin-test-junit:$versions.kotlin"
kotlin.plugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:$versions.kotlin"
kotlin.allopen = "org.jetbrains.kotlin:kotlin-allopen:$versions.kotlin"
deps.kotlin = kotlin

def butterknife = [:]
butterknife.core = "com.jakewharton:butterknife:$versions.butterknife"
butterknife.compiler = "com.jakewharton:butterknife-compiler:$versions.butterknife"
deps.butterknife = butterknife
```

root build.gradle
```
buildscript {
    apply from: 'versions.gradle'
    dependencies {
        classpath deps.android_gradle_plugin
        classpath deps.kotlin.plugin
        classpath deps.butterknife_gradle_plugin
    }
}
```