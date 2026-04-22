@Entity
@Getter
@NoArgsConstructor
public class EventEntity {

    @Id
    @GeneratedValue
    private Long id;

    private Long aggregateId;
    private String type;
    private String payload;
    private Instant occurredAt;
}