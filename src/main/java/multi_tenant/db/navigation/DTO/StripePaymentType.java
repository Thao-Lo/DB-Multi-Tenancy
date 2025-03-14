package multi_tenant.db.navigation.DTO;

public enum StripePaymentType {
	SUBSCRIPTION, ADDITIONAL_ADMIN, ADDITIONAL_TENANT;

	public static StripePaymentType fromString(String type) {
		try {
			return StripePaymentType.valueOf(type.toUpperCase()); // check input with enum
		} catch (IllegalArgumentException e) {
			throw new IllegalStateException("Invalid payment type: " + type);
		}
	}
}
