package tw.waterballsa.order.domain;

public class Customer {
    private String name;
    private MembershipLevel membershipLevel;

    public Customer(String name) {
        this(name, MembershipLevel.REGULAR);
    }

    public Customer(String name, MembershipLevel membershipLevel) {
        this.name = name;
        this.membershipLevel = membershipLevel;
    }

    public String getName() {
        return name;
    }

    public MembershipLevel getMembershipLevel() {
        return membershipLevel;
    }

    public enum MembershipLevel {
        REGULAR(1.0),
        VIP(0.95);

        private final double discountRate;

        MembershipLevel(double discountRate) {
            this.discountRate = discountRate;
        }

        public double getDiscountRate() {
            return discountRate;
        }
    }
}
