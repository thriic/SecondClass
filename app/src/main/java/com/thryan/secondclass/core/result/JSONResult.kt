package com.thryan.secondclass.core.result

import org.json.JSONObject

data class JSONResult<T>(val message: String, val data:T)