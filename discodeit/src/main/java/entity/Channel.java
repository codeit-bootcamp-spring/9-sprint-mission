package entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Channal {
    private final UUID id = UUID.randomUUID();
    private long createdAt;
    private long updatedAt;

    private String name = "";

    private List<UUID> members = new ArrayList<>();

    Channal(String name){
        this.name = name;
        this.createdAt = this.updatedAt = System.currentTimeMillis();
    }

    public UUID getId() {
        return id;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public String getName() {
        return name;
    }

    public void updateUpdateAt(){
        this.updatedAt = System.currentTimeMillis();
    }

    public void UpdateName(String name){
        this.name = name;
        this.updateUpdateAt();
    }

    public void AddMember(UUID id){
        members.add(id);
        updateUpdateAt();
    }

    public void RemoveMember(UUID id){
        boolean ret = members.remove(id);

        if (ret == false){
            // 실패 시 코드
        }
        else {
            // 성공 시 코드
            updateUpdateAt();
        }
    }
}
