package org.cutlery.loader

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.cutlery.model.Table
import org.cutlery.model.column.Column
import org.cutlery.model.column.NumberColumn
import org.cutlery.model.column.StringColumn
import org.cutlery.model.column.TableColumn
import java.io.File

class YAMLLoader : Loader {
	override fun load(file: File): Table {
		val yamlMapper = ObjectMapper(YAMLFactory()).registerKotlinModule()
		val data: Map<String, Any> = yamlMapper.readValue(file, Map::class.java) as Map<String, Any>

		return Table.fromMap(data)
	}
}