import java.util.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;



class K2530681_EcoRideSystem {
    private List<K2530681_Customer> customerList;
    private List<K2530681_Car> carList;
    private List<K2530681_Reservation> reservationList;
    private Scanner scanner;
    private static List<K2530681_RentalPackage> packageList;
    private static final double DEPOSIT = 5000.0;
    public static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    public K2530681_EcoRideSystem() {
        customerList = new ArrayList<>();
        carList = new ArrayList<>();
        reservationList = new ArrayList<>();
        scanner = new Scanner(System.in);
        packageList = new ArrayList<>();
        initializePackages();
    }

    private void initializePackages() {
        // categoryName, dailyRentalFee, freeKmPerDay, extraKmCharge, taxRate, discountRate
        packageList.add(new K2530681_RentalPackage("Compact Petrol", 5000, 100, 50, 10, 10));
        packageList.add(new K2530681_RentalPackage("Hybrid", 7500, 150, 60, 12, 10));
        packageList.add(new K2530681_RentalPackage("Electric", 10000, 200, 40, 8, 10));
        packageList.add(new K2530681_RentalPackage("Luxury SUV", 15000, 250, 75, 15, 10));
    }

    public static List<K2530681_RentalPackage> getPackageList() {
        return packageList;
    }

    // Register customer
    public void registerCustomer() {
        System.out.println("\n=== Register Customer ===");
        System.out.print("Enter customer type (1-Local / 2-Foreign): ");
        int type = readIntInput();

        System.out.print("Enter name: ");
        String name = scanner.nextLine().trim();
        System.out.print("Enter email: ");
        String email = scanner.nextLine().trim();
        System.out.print("Enter phone number: ");
        String phone = scanner.nextLine().trim();

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty()) {
            System.out.println("Name, email and phone are required.");
            return;
        }
        if (!isValidEmail(email)) {
            System.out.println("Invalid email format.");
            return;
        }

        K2530681_Customer customer = null;
        if (type == 1) {
            System.out.print("Enter NIC (9digits+V/X or 12digits): ");
            String nic = scanner.nextLine().trim();
            if (!isValidNic(nic)) {
                System.out.println("Invalid NIC format.");
                return;
            }
            customer = new K2530681_LocalCustomer(name, email, phone, nic);
        } else if (type == 2) {
            System.out.print("Enter Passport (5-9 alphanumeric): ");
            String passport = scanner.nextLine().trim();
            if (!isValidPassport(passport)) {
                System.out.println("Invalid passport format.");
                return;
            }
            customer = new K2530681_ForeignCustomer(name, email, phone, passport);
        } else {
            System.out.println("Invalid customer type.");
            return;
        }

        customer.registerCustomer();
        customerList.add(customer);
        System.out.println("Customer registered successfully! ID: " + customer.getCustomer_id());
    }

    public void viewAllCustomers() {
        System.out.println("\n=== All Customers ===");
        if (customerList.isEmpty()) {
            System.out.println("No customers registered.");
            return;
        }
        for (K2530681_Customer c : customerList) {
            System.out.println("ID: " + c.getCustomer_id() + " | Name: " + c.getCustomer_name() +
                    " | Email: " + c.getEmail() + " | Phone: " + c.getPhoneNo());
        }
    }

    public void addCar() {
        System.out.println("\n=== Add Car ===");
        System.out.print("Enter Car ID: ");
        String carId = scanner.nextLine().trim();
        if (carId.isEmpty()) {
            System.out.println("Car ID cannot be empty.");
            return;
        }
        if (findCarIdById(carId) != null) {
            System.out.println("Car ID already exists.");
            return;
        }

        System.out.print("Enter Model: ");
        String model = scanner.nextLine().trim();

        System.out.println("Available Categories:");
        for (int i = 0; i < packageList.size(); i++) {
            System.out.println((i + 1) + ". " + packageList.get(i).getCategoryName());
        }
        System.out.print("Select category (1-" + packageList.size() + "): ");
        int sel = readIntInput();
        if (sel < 1 || sel > packageList.size()) {
            System.out.println("Invalid selection.");
            return;
        }
        K2530681_RentalPackage pkg = packageList.get(sel - 1);
        K2530681_Car car = new K2530681_Car(carId, model, "Available", pkg);
        carList.add(car);
        System.out.println("Car added successfully.");
    }

    public void viewAvailableCars() {
        System.out.println("\n=== Available Cars ===");
        boolean any = false;
        for (K2530681_Car car : carList) {
            if ("Available".equalsIgnoreCase(car.getStatus())) {
                System.out.println(car.getCar_id() + " | " + car.getModel() + " | " +
                        car.getRentalPackage().getCategoryName() + " | LKR " + car.getRentalPackage().getDailyRentalFee());
                any = true;
            }
        }
        if (!any) System.out.println("No available cars.");
    }

    public void updateCarStatus() {
        System.out.println("\n=== Update Car Status ===");
        System.out.print("Enter Car ID: ");
        String carId = scanner.nextLine().trim();
        K2530681_Car car = findCarIdById(carId);
        if (car == null) {
            System.out.println("Car not found.");
            return;
        }
        System.out.print("Enter new status (Available/Reserved/Under Maintenance): ");
        String status = scanner.nextLine().trim();
        if (!(status.equalsIgnoreCase("Available") || status.equalsIgnoreCase("Reserved")
                || status.equalsIgnoreCase("Under Maintenance"))) {
            System.out.println("Invalid status.");
            return;
        }
        car.setStatus(status);
        System.out.println("Status updated.");
    }

    public void makeReservation() {
        System.out.println("\n=== Make Reservation ===");
        System.out.print("Enter customer name: ");
        String name = scanner.nextLine().trim();
        K2530681_Customer customer = findCustomerByName(name);
        if (customer == null) {
            System.out.println("Customer not found.");
            return;
        }

        System.out.print("Enter Car ID: ");
        String carId = scanner.nextLine().trim();
        K2530681_Car car = findCarIdById(carId);
        if (car == null) {
            System.out.println("Car not found.");
            return;
        }
        if (!"Available".equalsIgnoreCase(car.getStatus())) {
            System.out.println("Car is not available.");
            return;
        }

        try {
            System.out.print("Enter start date (dd-MM-yyyy): ");
            LocalDate start = LocalDate.parse(scanner.nextLine().trim(), DTF);
            System.out.print("Enter end date (dd-MM-yyyy): ");
            LocalDate end = LocalDate.parse(scanner.nextLine().trim(), DTF);

            System.out.print("Enter total kilometers (non-negative): ");
            double mileage = Double.parseDouble(scanner.nextLine().trim());
            if (mileage < 0) {
                System.out.println("Mileage cannot be negative.");
                return;
            }

            LocalDate today = LocalDate.now();

            if (!start.isBefore(end)) {
                System.out.println("Start date must be before end date.");
                return;
            }

            long daysToStart = ChronoUnit.DAYS.between(today, start);
            if (daysToStart < 3) {
                System.out.println("Booking must be at least 3 days before start date.");
                return;
            }

            // check overlaps for same car
            for (K2530681_Reservation r : reservationList) {
                if (r.getCar().getCar_id().equals(car.getCar_id()) && r.isActive()) {
                    if (!(end.isBefore(r.getStartDate()) || start.isAfter(r.getEndDate()))) {
                        System.out.println("Requested dates overlap an existing reservation (ID: " + r.getReservationId() + ").");
                        return;
                    }
                }
            }

            String resId = "RES" + (reservationList.size() + 1);
            K2530681_Reservation reservation = new K2530681_Reservation(resId, start, end, mileage, customer, car, LocalDate.now(), DEPOSIT);
            reservationList.add(reservation);
            car.setStatus("Reserved");
            System.out.println("Reservation created. ID: " + resId);

        } catch (DateTimeParseException e) {
            System.out.println("Invalid date format. Use dd-MM-yyyy.");
        } catch (NumberFormatException e) {
            System.out.println("Invalid number for mileage.");
        }
    }

    public void cancelReservation() {
        System.out.println("\n=== Cancel Reservation ===");
        System.out.print("Enter reservation ID: ");
        String id = scanner.nextLine().trim();
        K2530681_Reservation r = searchReservationById(id);
        if (r == null) {
            System.out.println("Reservation not found.");
            return;
        }
        long daysSinceBooking = ChronoUnit.DAYS.between(r.getBookingDate(), LocalDate.now());
        if (daysSinceBooking > 2) {
            System.out.println("Cannot cancel after 2 days.");
            return;
        }
        r.cancelBooking();
        r.getCar().setStatus("Available");
        System.out.println("Reservation cancelled.");
    }

    public void updateReservation() {
        System.out.println("\n=== Update Reservation ===");
        System.out.print("Enter reservation ID: ");
        String id = scanner.nextLine().trim();
        K2530681_Reservation r = searchReservationById(id);
        if (r == null) {
            System.out.println("Reservation not found.");
            return;
        }
        long daysSinceBooking = ChronoUnit.DAYS.between(r.getBookingDate(), LocalDate.now());
        if (daysSinceBooking > 2) {
            System.out.println("Cannot update after 2 days.");
            return;
        }
        System.out.print("Enter new mileage (non-negative): ");
        try {
            double m = Double.parseDouble(scanner.nextLine().trim());
            if (m < 0) {
                System.out.println("Mileage cannot be negative.");
                return;
            }
            r.setMileage(m);
            System.out.println("Reservation updated.");
        } catch (NumberFormatException e) {
            System.out.println("Invalid input for mileage.");
        }
    }

    public void viewAllReservations() {
        System.out.println("\n=== All Reservations ===");
        if (reservationList.isEmpty()) {
            System.out.println("No reservations.");
            return;
        }
        for (K2530681_Reservation r : reservationList) {
            r.displayReservationDetails();
        }
    }

    public void searchReservationByName() {
        System.out.print("Enter customer name: ");
        String name = scanner.nextLine().trim();
        boolean found = false;
        for (K2530681_Reservation r : reservationList) {
            if (r.getCustomer().getCustomer_name().equalsIgnoreCase(name)) {
                r.displayReservationDetails();
                found = true;
            }
        }
        if (!found) System.out.println("No reservations for this customer.");
    }

    public K2530681_Reservation searchReservationById(String id) {
        for (K2530681_Reservation r : reservationList) {
            if (r.getReservationId().equalsIgnoreCase(id)) return r;
        }
        return null;
    }

    public void searchReservationByDate() {
        System.out.print("Enter date (dd-MM-yyyy): ");
        try {
            LocalDate date = LocalDate.parse(scanner.nextLine().trim(), DTF);
            boolean found = false;
            for (K2530681_Reservation r : reservationList) {
                if (!date.isBefore(r.getStartDate()) && !date.isAfter(r.getEndDate())) {
                    r.displayReservationDetails();
                    found = true;
                }
            }
            if (!found) System.out.println("No reservations on this date.");
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date format.");
        }
    }

    public void generateInvoice() {
        System.out.print("Enter reservation ID: ");
        String id = scanner.nextLine().trim();
        K2530681_Reservation r = searchReservationById(id);
        if (r == null) {
            System.out.println("Reservation not found.");
            return;
        }
        K2530681_Invoice inv = new K2530681_Invoice(r);
        inv.generateInvoice();
    }


    private int readIntInput() {
        try {
            String s = scanner.nextLine().trim();
            if (s.isEmpty()) return -1;
            return Integer.parseInt(s);
        } catch (Exception e) {
            return -1;
        }
    }

    private boolean isValidEmail(String email) {
        return email.matches("^\\S+@\\S+\\.\\S+$");
    }

    private boolean isValidNic(String nic) {
        return nic.matches("^(\\d{9}[vVxX]|\\d{12})$");
    }

    private boolean isValidPassport(String p) {
        return p.matches("^[A-Za-z0-9]{5,9}$");
    }

    
    public K2530681_Car findCarIdById(String id) {
        for (K2530681_Car c : carList) {
            if (c.getCar_id().equalsIgnoreCase(id)) return c;
        }
        return null;
    }

    public K2530681_Customer findCustomerByName(String name) {
        for (K2530681_Customer c : customerList) {
            if (c.getCustomer_name().equalsIgnoreCase(name)) return c;
        }
        return null;
    }

    // --- Menu & main ---

    public void showMenu() {
        while (true) {
            System.out.println("\n============================");
            System.out.println("   EcoRide Car Rental System");
            System.out.println("============================");
            System.out.println("1. Register Customer");
            System.out.println("2. View All Customers");
            System.out.println("3. Add Car");
            System.out.println("4. View Available Cars");
            System.out.println("5. Update Car Status");
            System.out.println("6. Make Reservation");
            System.out.println("7. Cancel Reservation");
            System.out.println("8. Update Reservation");
            System.out.println("9. View All Reservations");
            System.out.println("10. Search Reservation By Name");
            System.out.println("11. Search Reservation By ID");
            System.out.println("12. Search Reservation By Date");
            System.out.println("13. Generate Invoice");
            System.out.println("14. Exit");
            System.out.print("Enter choice: ");
            int choice = readIntInput();
            switch (choice) {
                case 1 -> registerCustomer();
                case 2 -> viewAllCustomers();
                case 3 -> addCar();
                case 4 -> viewAvailableCars();
                case 5 -> updateCarStatus();
                case 6 -> makeReservation();
                case 7 -> cancelReservation();
                case 8 -> updateReservation();
                case 9 -> viewAllReservations();
                case 10 -> searchReservationByName();
                case 11 -> {
                    System.out.print("Enter reservation ID: ");
                    String id = scanner.nextLine().trim();
                    K2530681_Reservation r = searchReservationById(id);
                    if (r != null) r.displayReservationDetails();
                    else System.out.println("Not found.");
                }
                case 12 -> searchReservationByDate();
                case 13 -> generateInvoice();
                case 14 -> {
                    System.out.println("Exiting.");
                    return;
                }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    public static void main(String[] args) {
        K2530681_EcoRideSystem sys = new K2530681_EcoRideSystem();
        sys.showMenu();
    }
}

// ------------------------ Customer classes -------------------------

abstract class K2530681_Customer {
    private String customer_id;
    private String customer_name;
    private String email;
    private String phone_No;

    public K2530681_Customer(String name, String email, String phone) {
        this.customer_name = name;
        this.email = email;
        this.phone_No = phone;
    }

    public abstract void registerCustomer();

    public String getCustomer_id() { return customer_id; }
    public void setCustomer_id(String id) { this.customer_id = id; }

    public String getCustomer_name() { return customer_name; }
    public void setCustomerName(String n) { this.customer_name = n; }

    public String getEmail() { return email; }

    
    public String getPhoneNo() { return phone_No; }
    public void setPhoneNo(String phone) { this.phone_No = phone; }
}

class K2530681_LocalCustomer extends K2530681_Customer {
    private String nic;

    public K2530681_LocalCustomer(String name, String email, String phone, String nic) {
        super(name, email, phone);
        this.nic = nic;
    }

    @Override
    public void registerCustomer() {
        // use nic in customer id
        setCustomer_id("LC-" + nic);
    }

    public String getNic() { return nic; }
    public void setNic(String nic) { this.nic = nic; }
}

class K2530681_ForeignCustomer extends K2530681_Customer {
    private String passport;

    public K2530681_ForeignCustomer(String name, String email, String phone, String passport) {
        super(name, email, phone);
        this.passport = passport;
    }

    @Override
    public void registerCustomer() {
        setCustomer_id("FC-" + passport);
    }

    public String getPassport() { return passport; }
    public void setPassport(String p) { this.passport = p; }
}

// ------------------------ Car class -------------------------

class K2530681_Car {
    private String model;
    private String car_id;
    private String status;
    private K2530681_RentalPackage rentalPackage;

    public K2530681_Car(String car_id, String model, String status, K2530681_RentalPackage pkg) {
        this.model = model;
        this.car_id = car_id;
        this.status = status;
        this.rentalPackage = pkg;
    }

    // UML exact method names:
    public void upDateStatus(boolean available) {
        this.status = available ? "Available" : "Reserved";
    }

    public void getRentalInfo() {
        System.out.println("Model: " + model + " | Category: " + rentalPackage.getCategoryName());
    }

    public String getModel() { return model; }
    public String getCar_id() { return car_id; }
    public String getStatus() { return status; }
    public K2530681_RentalPackage getRentalPackage() { return rentalPackage; }

    // UML exact setters
    public void setCarId(String id) { this.car_id = id; }
    public void setCarModel(String model) { this.model = model; }
    public void setStatus(String status) { this.status = status; }
}

// ------------------------ Reservation class -------------------------

class K2530681_Reservation {
    private String reservationId;
    private LocalDate startDate;
    private LocalDate endDate;
    private int duration;
    private double mileage;
    private LocalDate bookingDate;
    private String status;
    private double depositAmount;
    private K2530681_Customer customer;
    private K2530681_Car car;

    public K2530681_Reservation(String reservationId, LocalDate startDate, LocalDate endDate,
                                double mileage, K2530681_Customer customer, K2530681_Car car,
                                LocalDate bookingDate, double depositAmount) {
        this.reservationId = reservationId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.mileage = mileage;
        this.customer = customer;
        this.car = car;
        this.bookingDate = bookingDate;
        this.status = "Active";
        this.depositAmount = depositAmount;
        calculateDuration();
    }

    // UML methods preserved
    public void addBooking() {
        System.out.println("Booking added: " + reservationId);
    }

    public void updateBooking() {
        System.out.println("Booking updated: " + reservationId);
    }

    public String getReservationId() { return reservationId; }
    public void setReservationId(String id) { this.reservationId = id; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate d) { this.startDate = d; calculateDuration(); }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate d) { this.endDate = d; calculateDuration(); }

    public int getDuration() { return duration; }
    public void setDuration(int d) { this.duration = d; }

    public double getMileage() { return mileage; }
    public void setMileage(double m) { this.mileage = m; }

    public LocalDate getBookingDate() { return bookingDate; }
    public void setBookingDate(LocalDate d) { this.bookingDate = d; }

    public String getStatus() { return status; }
    public void setStatus(String s) { this.status = s; }

    public double getDeposit() { return depositAmount; }
    public void setDepositAmount(double d) { this.depositAmount = d; }

    public K2530681_Customer getCustomer() { return customer; }
    public K2530681_Car getCar() { return car; }

    public void calculateDuration() {
        long days = ChronoUnit.DAYS.between(startDate, endDate);
        this.duration = (int) days;
    }

    public void cancelBooking() {
        this.status = "Cancelled";
    }

    public double calculateTotalCost() {
        return car.getRentalPackage().calculateRentalCost(duration, mileage);
    }

    // NEW per UML: calculateTax(double subTotal, double taxRate)
    public double calculateTax(double subTotal, double taxRate) {
        return subTotal * (taxRate / 100.0);
    }

    public boolean isActive() {
        return "Active".equalsIgnoreCase(status);
    }

    public void displayReservationDetails() {
        System.out.println("\n--- Reservation Details ---");
        System.out.println("Reservation ID: " + reservationId);
        System.out.println("Customer: " + customer.getCustomer_name() + " (" + customer.getCustomer_id() + ")");
        System.out.println("Car: " + car.getModel() + " [" + car.getCar_id() + "]");
        System.out.println("Start Date: " + startDate.format(K2530681_EcoRideSystem.DTF));
        System.out.println("End Date: " + endDate.format(K2530681_EcoRideSystem.DTF));
        System.out.println("Duration: " + duration + " days");
        System.out.println("Mileage: " + mileage + " km");
        System.out.println("Booking Date: " + bookingDate.format(K2530681_EcoRideSystem.DTF));
        System.out.println("Status: " + status);
        System.out.println("Deposit: LKR " + depositAmount);
    }
}

// ------------------------ Invoice class -------------------------

class K2530681_Invoice {
    private String invoiceId;
    private double basePrice;
    private double extraKmFee;
    private double finalPayableAmount;
    private double discount;
    private double tax;
    private double deposit;
    private K2530681_Reservation reservation;

    public K2530681_Invoice(K2530681_Reservation reservation) {
        this.reservation = reservation;
        this.invoiceId = "INV-" + reservation.getReservationId();
        this.deposit = reservation.getDeposit();
    }

    // UML exact setter
    public void setInvoiceId(String id) { this.invoiceId = id; }

    public void generateInvoice() {
        K2530681_Car car = reservation.getCar();
        K2530681_RentalPackage pkg = car.getRentalPackage();
        int days = reservation.getDuration();
        double mileage = reservation.getMileage();

        basePrice = pkg.getDailyRentalFee() * days;

        double freeKm = pkg.getFreeKmPerDay() * days;
        extraKmFee = mileage > freeKm ? (mileage - freeKm) * pkg.getExtraKmCharge() : 0.0;

        discount = days >= 7 ? basePrice * (pkg.getDiscountRate() / 100.0) : 0.0;

        double subtotal = basePrice + extraKmFee - discount;

        // Use reservation.calculateTax per UML
        tax = reservation.calculateTax(subtotal, pkg.getTaxRate());

        finalPayableAmount = subtotal + tax - deposit;

        // Print invoice
        System.out.println("\n===== INVOICE =====");
        System.out.println("Invoice ID: " + invoiceId);
        System.out.println("Reservation ID: " + reservation.getReservationId());
        System.out.printf("Base Price: LKR %.2f%n", basePrice);
        System.out.printf("Extra Km Fee: LKR %.2f%n", extraKmFee);
        System.out.printf("Discount: LKR %.2f%n", discount);
        System.out.printf("Tax (%.2f%%): LKR %.2f%n", pkg.getTaxRate(), tax);
        System.out.printf("Deposit: LKR %.2f%n", deposit);
        System.out.printf("Final Payable Amount: LKR %.2f%n", finalPayableAmount);
        System.out.println("===================\n");
    }

    // getters
    public String getInvoiceId() { return invoiceId; }
    public double getBasePrice() { return basePrice; }
    public double getExtraKmFee() { return extraKmFee; }
    public double getFinalPayableAmount() { return finalPayableAmount; }
    public double getDiscount() { return discount; }
    public double getTax() { return tax; }
    public double getDeposit() { return deposit; }
}

// ------------------------ RentalPackage class -------------------------

class K2530681_RentalPackage {
    private String categoryName;
    private double dailyRentalFee;
    private double freeKmPerDay;
    private double extraKmCharge;
    private double taxRate;      
    private double discountRate; 

    public K2530681_RentalPackage(String categoryName, double dailyRentalFee,
                                  double freeKmPerDay, double extraKmCharge,
                                  double taxRate, double discountRate) {
        this.categoryName = categoryName;
        this.dailyRentalFee = dailyRentalFee;
        this.freeKmPerDay = freeKmPerDay;
        this.extraKmCharge = extraKmCharge;
        this.taxRate = taxRate;
        this.discountRate = discountRate;
    }

    public double calculateRentalCost(int days, double kmDriven) {
        double base = dailyRentalFee * days;
        double freeKm = freeKmPerDay * days;
        double extraKm = kmDriven > freeKm ? (kmDriven - freeKm) * extraKmCharge : 0.0;
        double discount = days >= 7 ? base * (discountRate / 100.0) : 0.0;
        double subtotal = base + extraKm - discount;
        double tax = subtotal * (taxRate / 100.0);
        return subtotal + tax;
    }

    public void displayCategoryInfo() {
        System.out.println("Category: " + categoryName);
        System.out.println("Daily Rate: " + dailyRentalFee);
        System.out.println("Free Km/Day: " + freeKmPerDay);
        System.out.println("Extra Km Charge: " + extraKmCharge);
        System.out.println("Tax Rate: " + taxRate + "%");
    }

    // Getters / Setters with UML exact names
    public String getCategoryName() { return categoryName; }
    public double getDailyRentalFee() { return dailyRentalFee; }
    public double getFreeKmPerDay() { return freeKmPerDay; }
    public double getExtraKmCharge() { return extraKmCharge; }

    // UML requires getTaxRate / setTaxRate
    public double getTaxRate() { return taxRate; }
    public void setTaxRate(double taxRate) { this.taxRate = taxRate; }

    // UML requires getDiscount / setDiscount
    public double getDiscount() { return discountRate; }
    public void setDiscount(double discount) { this.discountRate = discount; }

    // For internal invoice logic used earlier we also provide discount getter name used sometimes
    public double getDiscountRate() { return discountRate; }
}
