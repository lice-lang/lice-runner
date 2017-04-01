@file:JvmName("Main")

package org.lice.runner

import com.sun.java.swing.plaf.windows.WindowsLookAndFeel
import org.intellij.lang.annotations.Language
import org.lice.Lice
import org.lice.compiler.util.forceRun
import org.lice.repl.VERSION_CODE
import java.awt.BorderLayout
import java.awt.Font
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.event.WindowEvent
import java.awt.event.WindowListener
import java.io.File
import java.io.OutputStream
import java.io.PrintStream
import java.util.*
import javax.swing.*
import javax.swing.filechooser.FileFilter

/**
 * Created by ice1000 on 2017/4/2.
 *
 * @author ice1000
 */
fun main(args: Array<String>) {
	UIManager.setLookAndFeel(WindowsLookAndFeel())
	val p = Vector<File>()
	val config = File("config.txt")
	val f = JFileChooser()
	val output = JTextArea()
	forceRun { output.font = Font("Consolas", 0, 16) }
	if (config.exists()) {
		p.addAll(config
				.readText()
				.split("\n")
				.map { File(it.trim()) }
				.filter { f -> (f.name.endsWith(".lice")) }
		)
	} else config.createNewFile()
	val outputFrame = JFrame()
	outputFrame.run frame@ {
		setSize(500, 500)
		defaultCloseOperation = WindowConstants.DISPOSE_ON_CLOSE
		layout = BorderLayout()
		System.setOut(PrintStream(object : OutputStream() {
			override fun write(b: Int) = output.append(b.toChar().toString())
			override fun write(b: ByteArray) = output.append(String(b))
		}))
		System.setErr(PrintStream(object : OutputStream() {
			override fun write(b: Int) = output.append(b.toChar().toString())
			override fun write(b: ByteArray) = output.append(String(b))
		}))
		add(JScrollPane(output), BorderLayout.CENTER)
		isVisible = true
	}
	JFrame("Lice Runner $VERSION_CODE").run frame@ {
		setSize(500, 500)
		defaultCloseOperation = WindowConstants.DISPOSE_ON_CLOSE
		layout = BorderLayout()
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
			override fun windowClosed(e: WindowEvent?) = System.exit(0)
			override fun windowActivated(e: WindowEvent?) = Unit
			override fun windowDeactivated(e: WindowEvent?) = save()
			override fun windowOpened(e: WindowEvent?) = Unit
			override fun windowIconified(e: WindowEvent?) = Unit
		})
		val ls = JList<File>(p)
		ls.addMouseListener(object : MouseAdapter() {
			override fun mouseClicked(e: MouseEvent?) {
				if (e != null && e.clickCount >= 2) {
					try {
						val file = p[ls.locationToIndex(e.point)]
						outputFrame.title = file.name
						output.text = ""
						Lice.run(file)
					} catch (e: Exception) {
					}
				}
			}
		})
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
