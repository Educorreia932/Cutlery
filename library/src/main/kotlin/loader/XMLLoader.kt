package org.cutlery.loader

import com.fasterxml.jackson.databind.SerializationFeature
import org.cutlery.model.Table
import java.io.File

import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule

class XMLLoader : Loader {
	override fun load(file: File): Table {
		val xmlMapper = XmlMapper(
			JacksonXmlModule()
		).registerKotlinModule()
			.enable(SerializationFeature.WRAP_ROOT_VALUE)

		val data: Map<String, Any> = xmlMapper.readValue(file, Map::class.java) as Map<String, Any>

		return Table.fromMap(data)
	}
}