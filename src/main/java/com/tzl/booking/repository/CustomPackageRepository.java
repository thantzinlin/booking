package com.tzl.booking.repository;

import org.springframework.stereotype.Repository;

import com.tzl.booking.dto.PackageResponse;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Repository
public class CustomPackageRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public List<PackageResponse> getAvailablePackagesForUser(Long userId, String countryparam) throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT id, name, credit, price, country, expiry_date FROM packages p ")
                .append("WHERE p.is_active = true ")
                .append("AND NOT EXISTS ( ")
                .append("  SELECT 1 FROM user_packages up ")
                .append("  WHERE up.package_id = p.id AND up.user_id = :userId ")
                .append(")");

        if (countryparam != null && !countryparam.trim().isEmpty()) {
            sb.append(" AND p.country = :country ");
        }

        Query query = entityManager.createNativeQuery(sb.toString());
        query.setParameter("userId", userId);

        if (countryparam != null && !countryparam.trim().isEmpty()) {
            query.setParameter("country", countryparam);
        }

        List<Object[]> rawResults = query.getResultList();

        List<PackageResponse> results = new ArrayList<>();
        for (Object[] row : rawResults) {
            Long id = ((Number) row[0]).longValue();
            String name = (String) row[1];
            Integer credit = ((Number) row[2]).intValue();
            BigDecimal price = (BigDecimal) row[3];
            String country = (String) row[4];
            Date expiryDate = (Date) row[5];

            results.add(new PackageResponse(id, name, credit, price, country, expiryDate));
        }

        return results;
    }

}
