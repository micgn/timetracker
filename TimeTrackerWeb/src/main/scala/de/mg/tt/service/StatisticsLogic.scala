package de.mg.tt.service

import _root_.java.math.RoundingMode
import _root_.java.text.DecimalFormat
import javax.ejb.Stateless
import javax.inject.Inject
import javax.interceptor.Interceptors

import de.mg.tt.model.Activity
import de.mg.tt.service.dao.TTMgmtDao
import de.mg.tt.util.DateHelper

/**
  * Created by michael on 27.12.15.
  */
@Stateless
@Interceptors(Array(classOf[ExceptionHandler]))
class StatisticsLogic {

  @Inject
  var dao: TTMgmtDao = null

  def buildStatisticsCsv: String = {
    var csv = "";
    createStatistics().foreach( { case (v1, v2, v3, v4, v5, v6) =>
      csv += v1 + ";" + v2 + ";" + v3 + ";" + v4 + ";" + v5+ ";" + v6 + "\n" } )
    csv
  }

  val NO_MONTH = -1

  private def createStatistics():  List[(String, String, String, String, String, String)] = {
    val all = dao.findAllActivities()

    val yearMap = createStatistics(all, a => (a.from.getYear, NO_MONTH))
    val monthMap = createStatistics(all, a => (a.from.getYear, a.from.getMonth))
    val periodMap = yearMap ++ monthMap
    val sortedPeriods = (periodMap.keys).toList.sortWith( { case ((y1, m1), (y2, m2)) => y1 < y2 || (y1 == y2 && m1 < m2) })

    val tuples =
      sortedPeriods.map( { case period => {
        val tup = periodMap.get(period).get
        (periodStr(period), doubleToStr(tup._1), tup._2.toString, doubleToStr(tup._3), doubleToStr(tup._4), doubleToStr(tup._5))
      }})

    List(("period", "total hours", "days", "8 hour days", "hour average per day", "maximum hours per day")) ++ tuples
  }

  private def createStatistics(all: List[Activity], groupByFun: Activity => (Int, Int)):
  Map[(Int, Int), (Double, Int, Double, Double, Double)] = {

    val groupedActivities = all.groupBy(groupByFun)

    val hourSumPerPeriod = groupedActivities.map { case (period, activities) => (period, sumHours(activities) ) }

    val daysPerPeriod = groupedActivities.map { case (period, activities) => (period, countDays(activities)) }

    val hourAveragePerPeriod = groupedActivities.map { case (period, activities) =>
      (period, hourSumPerPeriod.get(period).get / daysPerPeriod.get(period).get) }

    val eightHourDaysPerPeriod = groupedActivities.map { case (period, activities) =>
      (period, hourSumPerPeriod.get(period).get / 8.0) }

    val maxHoursPerPeriod = groupedActivities.map { case (period, activities) => (period, findMaxHoursPerDay(activities) ) }

    groupedActivities.map { case (period, activities) => (period,
      (hourSumPerPeriod.get(period).get,
        daysPerPeriod.get(period).get,
        eightHourDaysPerPeriod.get(period).get,
        hourAveragePerPeriod.get(period).get,
        maxHoursPerPeriod.get(period).get)) }
  }

  private def periodStr(period: (Int, Int)) =
    (if (period._2 != NO_MONTH) (period._2 + 1) + "." else "") + (period._1 - 100 + 2000).toString

  private def doubleToStr(d: Double) = {
    val df = new DecimalFormat("#.##"); df.setRoundingMode(RoundingMode.DOWN)
    df.format(d)
  }

  private def sumHours(activities: List[Activity]) = activities.foldLeft(0L)((sum: Long, activity: Activity) => sum + activity.len) / 60.0

  private def countDays(activities: List[Activity]) = activities.groupBy(a => DateHelper.dayStart(a.from)).size

  private def findMaxHoursPerDay(activities: List[Activity]) = activities.groupBy(a => DateHelper.dayStart(a.from)).
    map { case (date, activities) => (date, sumHours(activities)) }.values.max


}
