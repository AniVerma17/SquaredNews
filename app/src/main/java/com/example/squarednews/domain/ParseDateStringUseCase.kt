package com.example.squarednews.domain

import com.example.squarednews.Constants
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class ParseDateStringUseCase @Inject constructor() {
    operator fun invoke(dateTimeString: String): Date? = try {
        SimpleDateFormat(Constants.DATE_PATTERN).apply {
            timeZone = TimeZone.getTimeZone("IST")
        }.parse(dateTimeString)
    } catch (e: ParseException) {
        null
    }
}