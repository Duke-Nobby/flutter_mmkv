import 'dart:async';

import 'package:flutter/services.dart';

class MMKV {
  static const MethodChannel _channel = const MethodChannel('mmkv');

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  static Future<bool> init() async {
    final bool res = await _channel.invokeMethod('init');
    return res;
  }

  static Future<bool> saveValue(String key, dynamic value) async {
    assert(key != null);
    assert(value != null);
    final bool version =
        await _channel.invokeMethod('saveValue', {"key": key, "value": value});
    return version;
  }

  static Future<String> saveStringSet(String key, Set<String> value) async {
    assert(key != null);
    assert(value != null);
    final String version = await _channel
        .invokeMethod('saveStringSet', {"key": key, "value": value});
    return version;
  }

  static Future<Set<String>> getStringSet(
      String key, Set<String> defaultValue) async {
    assert(key != null);
    assert(defaultValue != null);
    Set<String> version = await _channel.invokeMethod(
        'getStringSet', {"key": key, "defaultValue": defaultValue});
    return version;
  }

  static Future<dynamic> getValueWithDefault(
      String key, String type, dynamic defaultValue) async {
    assert(key != null);
    assert(type != null);
    assert(defaultValue != null);
    final dynamic version = await _channel.invokeMethod('getValueWithDefault',
        {"key": key, "type": type, "defaultValue": defaultValue});
    return version;
  }

  static Future<dynamic> getValue(String key, String type) async {
    assert(key != null);
    assert(type != null);
    final dynamic version =
        await _channel.invokeMethod('getValue', {"key": key, "type": type});
    return version;
  }

  static Future<bool> clearAll() async {
    final bool version = await _channel.invokeMethod('clearAll');
    return version;
  }
}
