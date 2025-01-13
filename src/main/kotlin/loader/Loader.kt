package org.cutlery.loader

import org.cutlery.model.Table
import java.io.File

interface Loader {
	fun load(file: File): Table 
}