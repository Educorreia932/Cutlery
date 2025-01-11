package org.cutlery.utils

import java.nio.file.*
import java.nio.file.attribute.BasicFileAttributes

class GlobFileVisitor(glob: String) : SimpleFileVisitor<Path>() {
	private val pathMatcher: PathMatcher = FileSystems.getDefault().getPathMatcher("glob:$glob")
	private val matchedFiles: MutableList<Path> = ArrayList()

	override fun visitFile(path: Path, basicFileAttributes: BasicFileAttributes): FileVisitResult {
		if (pathMatcher.matches(path)) matchedFiles.add(path)

		return FileVisitResult.CONTINUE
	}

	fun getMatchedFiles(): List<Path> {
		return matchedFiles
	}
}