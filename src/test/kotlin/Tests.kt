import org.cutlery.dsl.dsl
import org.cutlery.dsl.merge
import org.cutlery.model.ColumnType
import kotlin.test.Test

class Tests {
	private val resourcesLocation = "src/test/resources"

	@Test
	fun `Example 1`() {
		val folder = "${resourcesLocation}/example1"

		dsl {
			load("${folder}/*.yaml")
			columns { withName("params") }
			save("example1.json")
		}
	}

	@Test
	fun `Example 2`() {
		val folder = "${resourcesLocation}/example2"

		merge(
//			dsl {
//				load("${folder}/vitis-report.xml")
//				columns { withName("AreaEstimates") }
//				unstack("AreaEstimates")
//			},
//			dsl {
//				load("${folder}/decision_tree.yaml")
//				columns { withType(ColumnType.TABLE) }
//			},
			dsl {
				load("${folder}/profiling.json")
				unstack("functions")
				sort("time%", false)
				rows(0)
			},
		)
			.save("example2.json")
	}

	@Test
	fun `Example 3`() {
		val folder = "${resourcesLocation}/example3"

		merge(
			dsl {
				load("${folder}/**/analysis.yaml")
			},
			dsl {
				load("${folder}/**/analysis.xml")
			},
			dsl {
				load("${folder}/**/profiling.json")
			}
		)
			.save("example3.json")
	}
}
