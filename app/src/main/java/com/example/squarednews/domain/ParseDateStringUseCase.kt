package com.example.squarednews.domain

import android.content.Context
import com.example.squarednews.Constants
import dagger.hilt.android.qualifiers.ApplicationContext
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class ParseDateStringUseCase @Inject constructor(
    @ApplicationContext private val context: Context
) {
    operator fun invoke(dateTimeString: String): Date? = try {
        SimpleDateFormat(Constants.DATE_PATTERN).apply {
            timeZone = TimeZone.getDefault()
        }.parse("$dateTimeString GMT")
    } catch (e: ParseException) {
        null
    }
}