package org.cutlery.loader

import kotlinx.serialization.ExperimentalSerializationApi
import org.cutlery.model.Table

import kotlinx.serialization.json.*
import org.cutlery.model.column.NumberColumn
import org.cutlery.model.column.StringColumn
import org.cutlery.model.column.TableColumn
import java.io.File

class JSONLoader : Loader {
	@OptIn(ExperimentalSerializationApi::class)
	override fun load(file: File): Table {
		val json = Json { ignoreUnknownKeys = true }
		val jsonElement: JsonElement = json.decodeFromStream(file.inputStream())

		return buildTable(jsonElement)
	}

	private fun buildTable(jsonElement: JsonElement): Table {
		when (jsonElement) {
			// Object
			is JsonObject -> {
				val table = Table()

				jsonElement.forEach { (key, value) ->
					val column = when (value) {
						is JsonPrimitive -> {
							if (value.isString)
								StringColumn(
									key,
									listOf(value.content)
								)
							
							else
								NumberColumn(
									key,
									listOf(value.float)
								)
						}

						is JsonArray -> {
							TableColumn(
								key,
								value.map { buildTable(it) }
							)
						}

						else -> throw IllegalArgumentException("Invalid JSON")
					}

					table.addColumn(column)
				}

				return table
			}

			// Array
			is JsonArray -> {
				val table = Table()

				jsonElement.forEach { element ->
					val column = StringColumn(
						element.jsonObject["name"].toString(),
						listOf(element.jsonObject["values"].toString())
					)

					table.addColumn(column)
				}

				return table
			}

			else -> throw IllegalArgumentException("Invalid JSON")
		}
	}
}