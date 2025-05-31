package controllers;

import models.ReportDAO;

public class ReportController {
        private final ReportDAO reportDAO;

        public ReportController(ReportDAO reportDAO) {
            this.reportDAO = reportDAO;
        }

        public int getTotalShipments(int year, int month) throws Exception {
            return reportDAO.getTotalShipments(year, month);
        }

        public int getCancelledShipments(int year, int month) throws Exception {
            return reportDAO.getCancelledShipments(year, month);
        }

        public double getOnTimeRate(int year, int month) throws Exception {
            return reportDAO.getOnTimeDeliveryRate(year, month);
        }

        public double getAvgDelay(int year, int month) throws Exception {
            return reportDAO.getAverageDelay(year, month);
        }

        public int getMaxDelay(int year, int month) throws Exception {
            return reportDAO.getDelay("max", year, month);
        }

        public int getMinDelay(int year, int month) throws Exception {
            return reportDAO.getDelay("min", year, month);
        }

        public double getSuccessRate(int year, int month) throws Exception {
            return reportDAO.getSuccessRate(year, month);
        }

        public double getFailureRate(int year, int month) throws Exception {
            return reportDAO.getFailureRate(year, month);
        }

        public double getAvgRating(int year, int month) throws Exception {
            return reportDAO.getAverageRating(year, month);
        }

        public int getRatingCount(int stars, int year, int month) throws Exception {
            return reportDAO.getRatingCount(stars, year, month);
        }

        public String getTopDriver(int year, int month) throws Exception {
            return reportDAO.getTopRatedDriver(year, month);
        }
}
