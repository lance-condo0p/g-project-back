package com.example

// TODO: reorganize common parts in tests
fun String.asResource(): String = object {}.javaClass.getResource(this).readText()