package com.veon.rafm.simbox.scheduler.utils

import java.io.InputStream
import java.util.concurrent.locks.ReentrantLock

import scala.io.Source

class LineBufferedStream(inputStream: InputStream) extends Logging {

  private[this] var _lines: IndexedSeq[String] = IndexedSeq()

  private[this] val _lock = new ReentrantLock()
  private[this] val _condition = _lock.newCondition()
  private[this] var _finished = false

  private val thread = new Thread {
    override def run() = {
      val lines = Source.fromInputStream(inputStream).getLines()
      for (line <- lines) {
        _lock.lock()
        try {
          trace("stdout: ", line)
          _lines = _lines :+ line
          _condition.signalAll()
        } finally {
          _lock.unlock()
        }
      }

      _lock.lock()
      try {
        _finished = true
        _condition.signalAll()
      } finally {
        _lock.unlock()
      }
    }
  }
  thread.setDaemon(true)
  thread.start()

  def lines: IndexedSeq[String] = _lines

  def iterator: Iterator[String] = {
    new LinesIterator
  }

  def waitUntilClose(): Unit = thread.join()

  private class LinesIterator extends Iterator[String] {
    private[this] var index = 0

    override def hasNext: Boolean = {
      if (index < _lines.length) {
        true
      } else {
        // Otherwise we might still have more data.
        _lock.lock()
        try {
          if (_finished) {
            false
          } else {
            _condition.await()
            index < _lines.length
          }
        } finally {
          _lock.unlock()
        }
      }
    }

    override def next(): String = {
      val line = _lines(index)
      index += 1
      line
    }
  }
}