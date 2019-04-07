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

### KotterKnife
Update 04/06/2019

kotlin use `@BindView(R2.id.view_id) lateinit val v: View` directly will NPE

Fortunately, the ButterKnife owner create a new Library [Kotterknife](https://github.com/JakeWharton/kotterknife) base on [butterknife-7.0.1](https://github.com/JakeWharton/butterknife)

```
// 7.0.1 or higher
def versions.butterknife = 10.0.0
implementation "com.jakewharton:butterknife:$versions.butterknife"
implementation "com.jakewharton:kotterknife:0.1.0-SNAPSHOT"
```

##### bindView
the `@BindView` will be replaced by `by bindView`
```
// @BindView(R2.id.index_vp)
// val mViewPager: ViewPager

// this code below is right//
var mViewPager: ViewPager by bindView(R.id.index_vp)
```
##### setValue
but as you can see, the bindView cannot be support, the IDE marked it in RED, info is
```
Missing 'setValue(IndexActivity, KProperty<*>, ViewPager)' method on delegate of type 'ReadOnlyProperty<Activity, ViewPager>'
```

What ?
according to the recommendation, generate a fun like this below
```
private operator fun Any.setValue(activity: Activity, property: KProperty<*>, v: View) {
    // just a blank, the error disappeared, so weird...
}
```
