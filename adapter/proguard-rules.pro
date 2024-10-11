# Protect all public classes, methods and fields in this AL adapter SDK
-keep class com.applovin.mediation.adapters.** { public *; }

# Repackage all classes into a single root-level package
-repackageclasses 'com.applovin.mediation.adapters.internal'
#-classobfuscationdictionary obf_dict.txt

-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*,!code/allocation/variable