-keep class com.kakao.sdk.** { *; }

# This rule will properly ProGuard all the model classes in
# the package com.yourcompany.models.
# Modify this rule to fit the structure of your app.
-keepclassmembers class com.mbj.doeat.** {
   *;
}
