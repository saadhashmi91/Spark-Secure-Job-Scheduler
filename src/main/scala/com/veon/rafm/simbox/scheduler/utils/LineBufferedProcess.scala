package com.veon.rafm.simbox.scheduler.utils

class LineBufferedProcess(process: Process) extends Logging {

  private[this] val _inputStream = new LineBufferedStream(process.getInputStream)
  private[this] val _errorStream = new LineBufferedStream(process.getErrorStream)

  def inputLines: IndexedSeq[String] = _inputStream.lines
  def errorLines: IndexedSeq[String] = _errorStream.lines

  def inputIterator: Iterator[String] = _inputStream.iterator
  def errorIterator: Iterator[String] = _errorStream.iterator

  def destroy(): Unit = {
    process.destroy()
  }

  /** Returns if the process is still actively running. */
  def isAlive: Boolean = Utils.isProcessAlive(process)

  def exitValue(): Int = {
    process.exitValue()
  }

  def waitFor(): Int = {
    val returnCode = process.waitFor()
    _inputStream.waitUntilClose()
    _errorStream.waitUntilClose()
    returnCode
  }
}