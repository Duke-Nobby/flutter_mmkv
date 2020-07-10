package com.common.mmkv

import android.content.Context
import android.os.Parcelable
import androidx.annotation.NonNull;

import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry.Registrar
import com.tencent.mmkv.MMKV
import android.util.Log


import java.lang.reflect.Type

/** MmkvPlugin */
public class MmkvPlugin: FlutterPlugin, MethodCallHandler {
    /// The MethodChannel that will the communication between Flutter and native Android
    ///
    /// This local reference serves to register the plugin with the Flutter Engine and unregister it
    /// when the Flutter Engine is detached from the Activity
    private lateinit var channel: MethodChannel
    private lateinit var mContext: Context

    override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        channel = MethodChannel(flutterPluginBinding.getFlutterEngine().getDartExecutor(), "mmkv")
        mContext = flutterPluginBinding.applicationContext;
        channel.setMethodCallHandler(this);
    }

    // This static function is optional and equivalent to onAttachedToEngine. It supports the old
    // pre-Flutter-1.12 Android projects. You are encouraged to continue supporting
    // plugin registration via this function while apps migrate to use the new Android APIs
    // post-flutter-1.12 via https://flutter.dev/go/android-project-migration.
    //
    // It is encouraged to share logic between onAttachedToEngine and registerWith to keep
    // them functionally equivalent. Only one of onAttachedToEngine or registerWith will be called
    // depending on the user's project. onAttachedToEngine or registerWith must both be defined
    // in the same class.
    companion object {
        @JvmStatic
        fun registerWith(registrar: Registrar) {
            val channel = MethodChannel(registrar.messenger(), "mmkv")
            channel.setMethodCallHandler(MmkvPlugin())
        }
    }

    override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
        when (call.method) {
            "getPlatformVersion" -> {
                result.success("Android ${android.os.Build.VERSION.RELEASE}")
            }
            "init" -> {
                MMKV.initialize(mContext)
                result.success(true)
            }
            "saveSetString" -> {
                val key = call.argument<String>("key")
                val value = call.argument<Set<String>>("value")
                MMKV.defaultMMKV().encode(key, value)
                result.success(true)
            }
            "saveValue" -> {
                val key = call.argument<String>("key")
                val isSuccess = when (val value = call.argument<Any>("value")) {
                    is String -> MMKV.defaultMMKV().encode(key, value)
                    is Float -> MMKV.defaultMMKV().encode(key, value)
                    is Double -> MMKV.defaultMMKV().encode(key, value)
                    is ByteArray -> MMKV.defaultMMKV().encode(key, value)
                    is Int -> MMKV.defaultMMKV().encode(key, value)
                    is Long -> MMKV.defaultMMKV().encode(key, value)
                    else -> false
                }
                result.success(isSuccess)
            }
            "getStringSet" -> {
                val key = call.argument<String>("key")!!
                val value = call.argument<Set<String>>("defaultValue")!!
                result.success(MMKV.defaultMMKV().decodeStringSet(key, value));
            }
            "getValueWithDefault" -> {
                val key = call.argument<String>("key")!!
                val type = call.argument<String>("type") ?: "String"
                val value = call.argument<Any>("defaultValue")!!
                when (type) {
                    "Int" -> {
                        result.success(MMKV.defaultMMKV().decodeInt(key, value as Int))
                    }
                    "Double" -> {
                        result.success(MMKV.defaultMMKV().decodeDouble(key, value as Double))
                    }
                    "Float" -> {
                        result.success(MMKV.defaultMMKV().decodeFloat(key, value as Float))
                    }
                    "ByteArray" -> {
                        result.success(MMKV.defaultMMKV().decodeBytes(key, value as ByteArray))
                    }
                    "String" -> {
                        val decodeString: String = MMKV.defaultMMKV().decodeString(key, value as String)
                        Log.e("decodeString", decodeString + "decodeString")
                        result.success(decodeString)
                    }
                    "Long" -> {
                        result.success(MMKV.defaultMMKV().decodeLong(key, value as Long))
                    }
                    "Boolean" -> {
                        result.success(MMKV.defaultMMKV().decodeBool(key, value as Boolean))
                    }
                }
            }
            "getValue" -> {
                val key = call.argument<String>("key")!!
                val type = call.argument<String>("type")!!
                when (type) {
                    "Int" -> {
                        result.success(MMKV.defaultMMKV().decodeInt(key) ?: 0)
                    }
                    "Double" -> {
                        result.success(MMKV.defaultMMKV().decodeDouble(key) ?: 0)
                    }
                    "Float" -> {
                        result.success(MMKV.defaultMMKV().decodeFloat(key) ?: 0)
                    }
                    "ByteArray" -> {
                        result.success(MMKV.defaultMMKV().decodeBytes(key) ?: ByteArray(0))
                    }
                    "String" -> {
                        result.success(MMKV.defaultMMKV().decodeString(key) ?: "")
                    }
                    "Long" -> {
                        result.success(MMKV.defaultMMKV().decodeLong(key) ?: 0)
                    }
                    "Boolean" -> {
                        result.success(MMKV.defaultMMKV().decodeBool(key) ?: false)
                    }
                }
            }
            "clearAll" -> {
                MMKV.defaultMMKV().clearAll()
                MMKV.defaultMMKV().clearMemoryCache()
                result.success(true)
            }
            else -> {
                result.notImplemented()
            }
//            "saveString" -> {
//                val key = call.argument<String>("key")
//                val value = call.argument<String>("value")
//                MMKV.defaultMMKV().encode(key, value!!)
//                result.success(true)
//            }
//            "saveBool" -> {
//                val key = call.argument<String>("key")
//                val argument = call.argument<Boolean>("value")
//                MMKV.defaultMMKV().encode(key, argument!!)
//                result.success(true)
//            }
//            "saveInt" -> {
//                val key = call.argument<String>("key")
//                val argument = call.argument<Int>("value")
//                MMKV.defaultMMKV().encode(key, argument!!)
//                result.success(true)
//            }
//            "saveFloat" -> {
//                val key = call.argument<String>("key")
//                val argument = call.argument<Float>("value")
//                MMKV.defaultMMKV().encode(key, argument!!)
//                result.success(true)
//            }
//            "saveDouble" -> {
//                val key = call.argument<String>("key")
//                val argument = call.argument<Double>("value")
//                MMKV.defaultMMKV().encode(key, argument!!)
//                result.success(true)
//            }
//            "saveParcelable" -> {
//                val key = call.argument<String>("key")
//                val argument = call.argument<Parcelable>("value")
//                MMKV.defaultMMKV().encode(key, argument!!)
//                result.success(true)
//            }

        }
    }

    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
    }
}
