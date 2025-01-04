package pcd.ass_single.part1.rx

import io.reactivex.rxjava3.core.{BackpressureStrategy, Flowable}
import io.reactivex.rxjava3.subjects.PublishSubject
import pcd.ass_single.part1.common.Directory
import pcd.ass_single.part1.common.controller.{AbstractPdfCounterController, ComputationStateType}
import pcd.ass_single.part1.common.view.PdfCounterView

import java.util

class RxController extends AbstractPdfCounterController[PdfCounterView]:
  private val totalStream: PublishSubject[Int] = PublishSubject.create()

  override protected def startComputation(): Unit =
    val f: Flowable[Directory] = Flowable.create(emitter => ???, BackpressureStrategy.BUFFER);

  override protected def stopComputation(): Unit = ???

  override protected def suspendComputation(): Unit = ???

  override protected def resumeComputation(): Unit = ???

  override protected def startComputationTemplate(): Unit =
    state.compareThenAct(util.Set.of(ComputationStateType.IDLE), () =>
      setStateAndLog(ComputationStateType.STARTING))
    if state.get() != ComputationStateType.STARTING then
      return
    startComputation();
    setStateAndLog(ComputationStateType.RUNNING)

  override protected def doUntilCompletion(): Unit = ???
