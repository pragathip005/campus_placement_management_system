package com.crms.placement.service;

// SRP: StatisticsService is solely responsible for data aggregation.
// It does not handle HTTP responses or UI formatting.

// Facade Pattern: StatisticsService provides a single simplified interface over multiple
// repositories (StudentRepository, ApplicationRepository, CompanyRepository).
// The controller calls one service instead of querying multiple repos directly.

import com.crms.placement.dto.AdminStatsDto;
import com.crms.placement.dto.CompanyOfferDto;
import com.crms.placement.dto.PlacedStudentDto;
import com.crms.placement.model.Application;
import com.crms.placement.model.ApplicationStatus;
import com.crms.placement.model.Company;
import com.crms.placement.model.Student;
import com.crms.placement.repository.ApplicationRepository;
import com.crms.placement.repository.CompanyRepository;
import com.crms.placement.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StatisticsService {

    // CTC tier thresholds — DB stores CTC in full rupees (1 LPA = 100,000)
    private static final double DREAM_THRESHOLD  = 1_500_000; // >= 15 LPA
    private static final double TIER1_THRESHOLD  = 1_000_000; // >= 10 LPA
    private static final double TIER2_THRESHOLD  =   600_000; // >=  6 LPA
    // Tier 3: anything below TIER2_THRESHOLD

    private final StudentRepository studentRepository;
    private final ApplicationRepository applicationRepository;
    private final CompanyRepository companyRepository;

    public StatisticsService(StudentRepository studentRepository,
                              ApplicationRepository applicationRepository,
                              CompanyRepository companyRepository) {
        this.studentRepository = studentRepository;
        this.applicationRepository = applicationRepository;
        this.companyRepository = companyRepository;
    }

    // -----------------------------------------------------------------------
    // Main aggregator — loads data once, computes all stats (no redundant DB hits)
    // -----------------------------------------------------------------------
    public AdminStatsDto getAdminStats() {
        // Load OFFER_ACCEPTED applications with opportunity + company eagerly (single query)
        List<Application> acceptedApps =
                applicationRepository.findByStatusWithOpportunityAndCompany(ApplicationStatus.OFFER_ACCEPTED);

        // Load all students into a map for O(1) branch lookup
        Map<Integer, Student> studentMap = studentRepository.findAll()
                .stream()
                .collect(Collectors.toMap(s -> s.getStudentId().intValue(), s -> s));

        AdminStatsDto dto = new AdminStatsDto();

        dto.setTotalStudents(studentRepository.count());
        dto.setTotalPlaced(applicationRepository.countByStatus(ApplicationStatus.OFFER_ACCEPTED));
        dto.setPlacementPercentage(calcPercentage(dto.getTotalPlaced(), dto.getTotalStudents()));
        dto.setAverageCtc(calcAverageCtc(acceptedApps));
        dto.setHighestCtc(calcHighestCtc(acceptedApps));
        dto.setTotalCompanies(companyRepository.count());
        dto.setOfferAcceptanceRate(calcAcceptanceRate());
        dto.setPlacementsByBranch(calcByBranch(acceptedApps, studentMap));
        dto.setTopHiringCompanies(calcTopCompanies(acceptedApps));
        dto.setMonthlyPlacementTrend(calcMonthlyTrend(acceptedApps));
        dto.setOffersByCtcTier(calcCtcTiers(acceptedApps));
        dto.setApplicationPipeline(calcPipeline());
        dto.setPlacedStudentsList(calcPlacedStudents(acceptedApps, studentMap));
        dto.setCompaniesList(calcCompaniesList());

        return dto;
    }

    // -----------------------------------------------------------------------
    // Individual public methods (for REST endpoint / unit testing)
    // -----------------------------------------------------------------------

    public long getTotalStudents() { return studentRepository.count(); }

    public long getTotalPlaced() {
        return applicationRepository.countByStatus(ApplicationStatus.OFFER_ACCEPTED);
    }

    public double getPlacementPercentage() {
        return calcPercentage(getTotalPlaced(), getTotalStudents());
    }

    public double getAverageCtc() {
        return calcAverageCtc(
                applicationRepository.findByStatusWithOpportunityAndCompany(ApplicationStatus.OFFER_ACCEPTED));
    }

    public double getHighestCtc() {
        return calcHighestCtc(
                applicationRepository.findByStatusWithOpportunityAndCompany(ApplicationStatus.OFFER_ACCEPTED));
    }

    public long getTotalCompanies() { return companyRepository.count(); }

    public Map<String, Long> getPlacementsByBranch() {
        List<Application> apps =
                applicationRepository.findByStatusWithOpportunityAndCompany(ApplicationStatus.OFFER_ACCEPTED);
        Map<Integer, Student> studentMap = studentRepository.findAll()
                .stream()
                .collect(Collectors.toMap(s -> s.getStudentId().intValue(), s -> s));
        return calcByBranch(apps, studentMap);
    }

    public List<CompanyOfferDto> getTopHiringCompanies() {
        return calcTopCompanies(
                applicationRepository.findByStatusWithOpportunityAndCompany(ApplicationStatus.OFFER_ACCEPTED));
    }

    public Map<String, Long> getMonthlyPlacementTrend() {
        return calcMonthlyTrend(
                applicationRepository.findByStatusWithOpportunityAndCompany(ApplicationStatus.OFFER_ACCEPTED));
    }

    public Map<String, Long> getOffersByCtcTier() {
        return calcCtcTiers(
                applicationRepository.findByStatusWithOpportunityAndCompany(ApplicationStatus.OFFER_ACCEPTED));
    }

    // -----------------------------------------------------------------------
    // Private calculation helpers
    // -----------------------------------------------------------------------

    private double calcPercentage(long numerator, long denominator) {
        if (denominator == 0) return 0.0;
        return Math.round((numerator * 100.0 / denominator) * 10.0) / 10.0;
    }

    private double calcAverageCtc(List<Application> apps) {
        return apps.stream()
                .map(a -> a.getOpportunity() != null ? a.getOpportunity().getCtc() : null)
                .filter(Objects::nonNull)
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);
    }

    private double calcHighestCtc(List<Application> apps) {
        return apps.stream()
                .map(a -> a.getOpportunity() != null ? a.getOpportunity().getCtc() : null)
                .filter(Objects::nonNull)
                .mapToDouble(Double::doubleValue)
                .max()
                .orElse(0.0);
    }

    private double calcAcceptanceRate() {
        long accepted = applicationRepository.countByStatus(ApplicationStatus.OFFER_ACCEPTED);
        long rejected = applicationRepository.countByStatus(ApplicationStatus.OFFER_REJECTED);
        return calcPercentage(accepted, accepted + rejected);
    }

    private Map<String, Long> calcByBranch(List<Application> apps, Map<Integer, Student> studentMap) {
        return apps.stream()
                .map(a -> studentMap.get(a.getStudentId()))
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(
                        s -> s.getBranch() != null ? s.getBranch() : "Unknown",
                        Collectors.counting()
                ));
    }

    private List<CompanyOfferDto> calcTopCompanies(List<Application> apps) {
        Map<String, List<Application>> byCompany = apps.stream()
                .filter(a -> a.getOpportunity() != null && a.getOpportunity().getCompany() != null)
                .collect(Collectors.groupingBy(a -> a.getOpportunity().getCompany().getName()));

        return byCompany.entrySet().stream()
                .map(e -> {
                    double avg = e.getValue().stream()
                            .map(a -> a.getOpportunity().getCtc())
                            .filter(Objects::nonNull)
                            .mapToDouble(Double::doubleValue)
                            .average().orElse(0.0);
                    return new CompanyOfferDto(e.getKey(), e.getValue().size(), avg, classifyTier(avg));
                })
                .sorted(Comparator.comparingLong(CompanyOfferDto::getOfferCount).reversed())
                .collect(Collectors.toList());
    }

    private Map<String, Long> calcMonthlyTrend(List<Application> apps) {
        // Month order for the seasonal chart (Aug–Jul academic year)
        List<String> monthOrder = List.of(
                "Aug", "Sep", "Oct", "Nov", "Dec", "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul");
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MMM");

        Map<String, Long> raw = apps.stream()
                .filter(a -> a.getAppliedDate() != null)
                .collect(Collectors.groupingBy(
                        a -> a.getAppliedDate().format(fmt),
                        Collectors.counting()
                ));

        // Return in academic-year order, filling missing months with 0
        Map<String, Long> ordered = new LinkedHashMap<>();
        for (String m : monthOrder) {
            ordered.put(m, raw.getOrDefault(m, 0L));
        }
        return ordered;
    }

    private Map<String, Long> calcCtcTiers(List<Application> apps) {
        Map<String, Long> tiers = new LinkedHashMap<>();
        tiers.put("Dream",  0L);
        tiers.put("Tier 1", 0L);
        tiers.put("Tier 2", 0L);
        tiers.put("Tier 3", 0L);
        for (Application app : apps) {
            Double ctc = app.getOpportunity() != null ? app.getOpportunity().getCtc() : null;
            String tier = (ctc != null) ? classifyTier(ctc) : "Tier 3";
            tiers.merge(tier, 1L, Long::sum);
        }
        return tiers;
    }

    private Map<String, Long> calcPipeline() {
        // Shows count of applications at every stage — useful for funnel view
        Map<String, Long> pipeline = new LinkedHashMap<>();
        for (ApplicationStatus status : ApplicationStatus.values()) {
            pipeline.put(status.name(), applicationRepository.countByStatus(status));
        }
        return pipeline;
    }

    private List<PlacedStudentDto> calcPlacedStudents(List<Application> apps,
                                                       Map<Integer, Student> studentMap) {
        return apps.stream()
                .map(a -> {
                    Student s = studentMap.get(a.getStudentId());
                    if (s == null) return null;
                    String company = (a.getOpportunity() != null && a.getOpportunity().getCompany() != null)
                            ? a.getOpportunity().getCompany().getName() : "—";
                    double ctcLpa = a.getOpportunity() != null && a.getOpportunity().getCtc() != null
                            ? Math.round((a.getOpportunity().getCtc() / 100000.0) * 10.0) / 10.0 : 0.0;
                    return new PlacedStudentDto(
                            s.getName() != null ? s.getName() : "Unknown",
                            s.getBranch() != null ? s.getBranch() : "—",
                            company,
                            ctcLpa
                    );
                })
                .filter(Objects::nonNull)
                .sorted(Comparator.comparingDouble(PlacedStudentDto::getCtcLpa).reversed())
                .collect(Collectors.toList());
    }

    private List<Map<String, String>> calcCompaniesList() {
        return companyRepository.findAll().stream()
                .map(c -> {
                    Map<String, String> m = new LinkedHashMap<>();
                    m.put("name",     c.getName() != null ? c.getName() : "—");
                    m.put("industry", c.getIndustry() != null ? c.getIndustry() : "—");
                    return m;
                })
                .collect(Collectors.toList());
    }

    private String classifyTier(double ctc) {
        if (ctc >= DREAM_THRESHOLD)  return "Dream";
        if (ctc >= TIER1_THRESHOLD)  return "Tier 1";
        if (ctc >= TIER2_THRESHOLD)  return "Tier 2";
        return "Tier 3";
    }
}
