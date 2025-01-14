package org.cutlery.saver

import org.cutlery.model.Table
import java.io.File

interface Saver {
	fun save(table: Table, file: File)
}