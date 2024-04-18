package uk.ac.tees.mad.w9642974.utils

interface Downloader {
    fun downloadFile(url: String, fileName: String): Long
}