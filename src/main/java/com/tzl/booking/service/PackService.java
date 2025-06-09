package com.tzl.booking.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tzl.booking.dto.PackageResponse;
import com.tzl.booking.dto.PurchaseInfoRequest;
import com.tzl.booking.dto.UserPackageResponse;
import com.tzl.booking.entity.Pack;
import com.tzl.booking.entity.PaymentInfo;
import com.tzl.booking.entity.User;
import com.tzl.booking.entity.UserPackage;
import com.tzl.booking.enums.PackageStatus;
import com.tzl.booking.exception.ResourceNotFoundException;
import com.tzl.booking.repository.CustomPackageRepository;
import com.tzl.booking.repository.PackRepository;
import com.tzl.booking.repository.PaymentInfoRepository;
import com.tzl.booking.repository.UserInfoRepository;
import com.tzl.booking.repository.UserPackageRepository;

import jakarta.transaction.Transactional;

@Service
public class PackService {
    @Autowired
    private PackRepository packageRepo;

    @Autowired
    private UserPackageRepository userPackageRepo;

    @Autowired
    private UserInfoRepository userInfoRepository;

    @Autowired
    private PaymentInfoRepository paymentInfoRepository;

    @Autowired
    private CustomPackageRepository customPackageRepository;

    public List<PackageResponse> getAvailablePackagesByCountry(String country, String email) throws Exception {
        User user = userInfoRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<PackageResponse> availablePacks = customPackageRepository.getAvailablePackagesForUser(user.getId(),
                country);

        if (availablePacks.isEmpty()) {
            throw new ResourceNotFoundException("No available packages found for country: " + country);
        }

        return availablePacks;
    }

    public List<UserPackageResponse> getMyPackage(String email) {
        User user = userInfoRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<UserPackage> userPackages = userPackageRepo.findByUser(user);
        if (userPackages.isEmpty()) {
            throw new ResourceNotFoundException("You have no packages yet");
        }

        return userPackages.stream()
                .map(this::mapToUserPackageResponse)
                .collect(Collectors.toList());
    }

    private UserPackageResponse mapToUserPackageResponse(UserPackage userPackage) {
        Pack pkg = userPackage.getPkg();

        return UserPackageResponse.builder()
                .id(userPackage.getId())
                .packageId(pkg.getId())
                .country(pkg.getCountry())
                .packageName(pkg.getName())
                .remainingCredits(userPackage.getRemainingCredit())
                .status(calculatePackageStatus(userPackage))
                .purchaseDate(userPackage.getPurchasedDate())
                .expiryDate(pkg.getExpiryDate())
                .build();
    }

    // private PackageStatus calculatePackageStatus(UserPackage userPackage) {
    // if (userPackage.getRemainingCredit() <= 0) {
    // return PackageStatus.EXPIRED;
    // }
    // return userPackage.getStatus();
    // }

    private PackageStatus calculatePackageStatus(UserPackage userPackage) {
        return userPackage.getPkg().getExpiryDate().isBefore(LocalDateTime.now())
                ? PackageStatus.EXPIRED
                : PackageStatus.ACTIVE;
    }

    @Transactional
    public void purchasePackage(String email, PurchaseInfoRequest request) {
        User user = userInfoRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Pack pack = packageRepo.findByIdAndIsActiveTrue(request.getPackageId())
                .orElseThrow(() -> new RuntimeException("Package not found or inactive"));

        if (pack.getPrice().compareTo(request.getAmount()) != 0) {
            throw new RuntimeException("Invalid amount. Please check the package price.");
        }

        boolean alreadyPurchased = userPackageRepo.existsByUserAndPkgId(user, request.getPackageId());
        if (alreadyPurchased) {
            throw new RuntimeException("You have already purchased this package.");
        }
        boolean paymentSuccessful = processPayment(user, pack.getPrice());

        if (!paymentSuccessful) {
            throw new RuntimeException("Payment failed. Please try again.");
        }
        UserPackage userPackage = new UserPackage();
        userPackage.setUser(user);
        userPackage.setPkg(pack);
        userPackage.setPurchasedDate(LocalDateTime.now());
        userPackage.setRemainingCredit(pack.getCredit());
        userPackage.setExpiryDate(pack.getExpiryDate());
        userPackage.setStatus(PackageStatus.ACTIVE);

        userPackageRepo.save(userPackage);

        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setCardNumber(request.getCardNumber());
        paymentInfo.setAmount(request.getAmount());
        paymentInfo.setCardHolder(request.getCardHolder());
        paymentInfo.setExpiryDate(request.getExpiryDate());
        paymentInfo.setPaymentMethod(request.getPaymentMethod());
        paymentInfo.setPaymentDate(LocalDateTime.now());
        paymentInfo.setUserPackage(userPackage);

        paymentInfoRepository.save(paymentInfo);

    }

    private boolean processPayment(User user, BigDecimal amount) {
        return true;
    }

}