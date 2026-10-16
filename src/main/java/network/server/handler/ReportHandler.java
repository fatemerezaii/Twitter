package network.server.handler;

import model.Report;
import network.shared.NetworkPacket;
import network.shared.RequestType;
import repository.ReportRepository;

import java.util.ArrayList;
import java.util.List;

public class ReportHandler {

    private final ReportRepository reportRepository = new ReportRepository();

    public NetworkPacket handle(NetworkPacket packet) {
        switch (packet.getRequestType()) {
            case REPORT_POST:
            case REPORT_USER:
                Report report = (Report) packet.getData();
                boolean saved = reportRepository.add(report);
                if (packet.getRequestType() == RequestType.REPORT_POST) {
                    return new NetworkPacket(RequestType.REPORT_POST_RESPONSE, saved);
                }

                return new NetworkPacket(RequestType.REPORT_USER_RESPONSE, saved);

            case GET_REPORTS:
                List<Report> reports = reportRepository.findAll();
                return new NetworkPacket(RequestType.GET_REPORTS_RESPONSE, reports);

            case UPDATE_REPORT_STATUS:
                Report updatedReport = (Report) packet.getData();
                boolean success = reportRepository.updateStatus(updatedReport.getReportId(), updatedReport.getStatus());
                return new NetworkPacket(RequestType.UPDATE_REPORT_STATUS_RESPONSE, success);

            default:
                return null;
        }
    }
}