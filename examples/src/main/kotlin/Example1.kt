package org.cutlery

import org.cutlery.dsl.concat
import org.cutlery.dsl.table

fun main() {
	val resourcesLocation = "examples/src/main/resources"
	val folder = "${resourcesLocation}/example1"

	concat(
		table {
			load("${folder}/decision_tree_1.yaml", true)
		},
		table {
			load("${folder}/decision_tree_2.yaml", true)
		},
		table {
			load("${folder}/decision_tree_3.yaml", true)
		}
	).apply {
		extract("params")
		columns { withName("criterion", "splitter", "ccp_alpha", "min_samples_split", "File") }
		rename("criterion", "Criterion")
		rename("splitter", "Splitter")
		rename("ccp_alpha", "CCP Alpha")
		rename("min_samples_split", "Min Samples Split")
		save("example1.json")
	}
}
