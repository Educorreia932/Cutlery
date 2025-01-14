package org.cutlery.saver

import kotlinx.serialization.json.*
import org.cutlery.model.Table
import org.cutlery.model.column.Column
import org.cutlery.model.column.NumberColumn
import org.cutlery.model.column.StringColumn
import org.cutlery.model.column.TableColumn
import java.io.File

class JSONSaver : Saver {
	override fun save(table: Table, file: File) {
		val result = toJsonElement(table)

		file.writeText(result.toString())
	}

	private fun toJsonElement(table: Table): JsonElement {
		val elements = mutableMapOf<String, JsonElement>()

		table.columns.forEach { column ->
			val jsonElement = toJsonElement(column)

			elements[column.name] = jsonElement
		}

		return JsonObject(elements)
	}

	private fun toJsonElement(column: Column<*>): JsonElement {
		return when (column) {
			is StringColumn ->
				if (column.size() == 1)
					JsonPrimitive(column.values.first())
				else
					JsonArray(column.values.map { JsonPrimitive(it) })

			is NumberColumn ->
				if (column.size() == 1)
					JsonPrimitive(column.values.first())
				else
					JsonArray(column.values.map { JsonPrimitive(it) })

			is TableColumn -> JsonArray(column.values.map { toJsonElement(it) })

			else -> throw IllegalArgumentException("Invalid column type")
		}
	}
}