package com.plr.github.wiki.sidebar

import java.util.regex.Pattern

class GenerateMenu {

	File wikiDir;
	static Pattern filePattern = ~/^(?!_).*.md/
	static Pattern headerPattern = ~/^(#+)\s*(.*)/
	def sidebarFile = "_Sidebar.md"
	
	public static void main(String[] args) {
		GenerateMenu gm = new GenerateMenu("/home/plr/workspace/Comparators.wiki")
		
		gm.run()
	}

	public GenerateMenu(String dir) {
		wikiDir = new File(dir)
	}
	
	def run() {
		
		def fileList = []

		File fileOut = new File(wikiDir, sidebarFile)

		File home

		wikiDir.eachFileMatch(filePattern) {file->
			String fName = file.name

			if (fName.startsWith("Home")) {
				home = file
			} else {
				fileList.add(file)
			}
		}

		fileList.sort()

		//Add Home a the beginning
		fileList.add(0,home)

		//erase current content
		fileOut.write "\n"
		
		fileList.each { file->

			String fName = file.name

			fName = fName.take(fName.lastIndexOf('.'))

			def lineOut = "* [${fName.replace('-',' ')}]($fName)\n"
			print lineOut
			fileOut.append lineOut

			int lastHeader = 0;


			def stack = [] as Stack

			file.eachLine { line ->

				def matcher = headerPattern.matcher(line)

				if (matcher.find()) {

					def header = matcher.group(1).size() as int

					String headerTitle = matcher.group(2)

					def anchor = headerTitle.toLowerCase().replace(' ','-')


					//			println  "lastHeader: $lastHeader header: $header"
//					if (lastHeader == header) {
//						stack.set(stack.size() - 1, address)
//					} else if (lastHeader < header) {
//						stack.push(address)
//					} else {
//						stack.pop()
//						stack.set(stack.size() - 1, address)
//					}

					lastHeader = header
//					def anchor = stack.join("_")
					
					def sp = " " as CharSequence

					lineOut = sp.multiply(header * 2) + "* [$headerTitle]($fName#${anchor})\n"
					print lineOut

					fileOut.append lineOut
				}
			}
		}


		executeGit("git add $sidebarFile")
		executeGit("git status")
		executeGit([
			"git",
			"commit",
			"-m \"new Sidebar\""] as String[])
	}

	private executeGit(String cmd) {
		String[] pizza = cmd.split(' ')
		println pizza
		executeGit(pizza)
	}

	private executeGit(String[] cmd) {
		def sout = new StringBuffer(), serr = new StringBuffer()
		def exitVal

		exitVal = cmd.execute(null, wikiDir)
		exitVal.consumeProcessOutput(sout, serr)
		exitVal.waitForOrKill(1000)
		println "exitValue: " + exitVal.exitValue()
		println "out> $sout"
		System.err.println "err> $serr"
	}
}
