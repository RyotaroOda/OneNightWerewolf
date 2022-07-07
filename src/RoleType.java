//Written by 小田竜太郎

public enum RoleType {
    Villager("村人", "lib/width=1000/CrowMark.png", true),
    Werewolf("人狼", "lib/width=1000/FangRed.png", false),
    FortuneTeller("占い師", "lib/width=1000/Taikyokuzu.png", true),
    PhantomThief("怪盗", "lib/width=1000/DominoMask.png", true);
    //Caution! ファイル名にスペースがあると読み取れない

    private String name;
    private String imageName;
    private boolean isVillagerTeam;

    private RoleType(String name, String imageName, boolean isVillagerTeam) {
        this.name = name;
        this.imageName = imageName;
        this.isVillagerTeam = isVillagerTeam;
    }

    public String getName() {
        return this.name;
    }

    public String getImageName() {
        return this.imageName;
    }

    public boolean getIsVillagerTeam() {
        return this.isVillagerTeam;
    }
}
