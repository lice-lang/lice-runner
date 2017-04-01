@file:JvmName("Main")

package org.lice.runner

import com.sun.java.swing.plaf.windows.WindowsLookAndFeel
import org.lice.repl.VERSION_CODE
import java.awt.BorderLayout
import java.io.File
import javax.swing.*
import javax.swing.filechooser.FileFilter

/**
 * Created by ice1000 on 2017/4/2.
 *
 * @author ice1000
 */
fun main(args: Array<String>) {
	val p = emptyList<String>().toMutableList()
	val config = File("config.properties")
	if (config.exists()) p.addAll(config.readText().split("\n|\r\n").toList())
	else config.createNewFile()
	UIManager.setLookAndFeel(WindowsLookAndFeel())
	JFrame("Lice Runner $VERSION_CODE").run frame@ {
		setSize(500, 500)
		defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
		layout = BorderLayout()
		val f = JFileChooser()
		f.fileFilter = object : FileFilter() {
			override fun getDescription() = "(.lice) lice source code"
			override fun accept(f: File?): Boolean {
				if (f != null) return f.name.endsWith(".lice") and f.isFile
				return false
			}
		}
		add(JScrollPane(JList<String>().apply {
		}), BorderLayout.CENTER)
		add(JButton("Add File...").apply {
			addActionListener { _ ->
				f.showDialog(this@frame, "Add a File to")
				f.selectedFile?.let { file ->

				}
			}
		}, BorderLayout.SOUTH)
		isVisible = true
	}
}
