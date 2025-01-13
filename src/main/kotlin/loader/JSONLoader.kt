package org.cutlery.loader

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.cutlery.model.Table

import java.io.File

class JSONLoader : Loader {
	override fun load(file: File): Table {
		val xmlMapper = jacksonObjectMapper()
		val data: Map<String, Any> = xmlMapper.readValue(file, Map::class.java) as Map<String, Any>

		return Table.fromMap(data)
	}
}