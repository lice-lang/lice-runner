@file:JvmName("Main")

package org.lice.runner

import com.sun.java.swing.plaf.windows.WindowsLookAndFeel
import org.lice.repl.VERSION_CODE
import java.awt.BorderLayout
import java.awt.event.WindowEvent
import java.awt.event.WindowListener
import java.io.File
import java.util.*
import javax.swing.*
import javax.swing.filechooser.FileFilter

/**
 * Created by ice1000 on 2017/4/2.
 *
 * @author ice1000
 */
fun main(args: Array<String>) {
	val p = Vector<File>()
	val config = File("config.txt")
	if (config.exists()) p.addAll(config
			.readText()
			.split("\n|\r\n")
			.map(::File)
			.filter { f -> f.name.endsWith(".lice") and f.isFile })
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
				if (f != null) return f.name.endsWith(".lice") or f.isDirectory
				return false
			}
		}
		val save = {
			config.writeText(p.fold(StringBuilder(), { sb, file ->
				sb.append(file.absolutePath).append("\n")
			}).toString())
		}
		addWindowListener(object : WindowListener {
			override fun windowDeiconified(e: WindowEvent?) = Unit
			override fun windowClosing(e: WindowEvent?) = save()
			override fun windowClosed(e: WindowEvent?) = Unit
			override fun windowActivated(e: WindowEvent?) = Unit
			override fun windowDeactivated(e: WindowEvent?) = save()
			override fun windowOpened(e: WindowEvent?) = Unit
			override fun windowIconified(e: WindowEvent?) = Unit
		})
		val ls = JList<File>(p)
		add(JScrollPane(ls), BorderLayout.CENTER)
		add(JButton("Add File...").apply {
			addActionListener { _ ->
				f.showDialog(this@frame, "Add a File")
				f.selectedFile?.let { file ->
					if (file !in p) p.add(file)
					ls.setListData(p)
				}
			}
		}, BorderLayout.NORTH)
		isVisible = true
	}
}
