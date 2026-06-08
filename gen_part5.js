// Part 5 - Teams + Players + Generation logic
const teamDefs = [
{id:'T001',n:'重庆狼队',sh:'Wolves',r:'重庆',c:'林',ca:'Fly',mem:['Fly','小胖','向鱼','妖刀','一笙'],mh:['吕布','赵云','王昭君','孙尚香','蔡文姬'],ho:['KPL知名战队','多次获得职业赛事冠军'],wr:0.72,t:120,de:'KPL知名强队，团队运营和团战能力突出。'},
{id:'T002',n:'成都AG超玩会',sh:'AG',r:'成都',c:'奶茶',ca:'一诺',mem:['一诺','长生','轩染','钟意','Cat'],mh:['孙尚香','马可波罗','小乔','亚瑟','蔡文姬'],ho:['KPL人气战队','职业赛事冠军队伍'],wr:0.68,t:115,de:'KPL高人气战队，选手个人能力和话题度较高。'},
{id:'T003',n:'武汉eStarPro',sh:'eStarPro',r:'武汉',c:'SK',ca:'花海',mem:['花海','清融','坦然','易峥','子阳'],mh:['韩信','貂蝉','吕布','后羿','蔡文姬'],ho:['KPL知名战队','多冠战队代表'],wr:0.74,t:130,de:'KPL冠军战队代表，体系成熟且执行力强。'},
{id:'T004',n:'北京WB',sh:'WB',r:'北京',c:'花楼',ca:'暖阳',mem:['暖阳','梓墨','花卷','乔兮','星宇'],mh:['澜','关羽','上官婉儿','公孙离','大乔'],ho:['KPL知名战队','2023春季赛亚军'],wr:0.65,t:110,de:'KPL劲旅，打野选手暖阳为核心。'},
{id:'T005',n:'佛山DRG',sh:'DRG',r:'佛山',c:'择',ca:'青枫',mem:['青枫','鹏鹏','百兽','梦岚','阿豆'],mh:['周瑜','赵云','马超','马可波罗','张飞'],ho:['KPL知名战队','多次四强'],wr:0.60,t:108,de:'KPL老牌强队，团队配合默契。'},
{id:'T006',n:'杭州LGD',sh:'LGD',r:'杭州',c:'张角',ca:'江城',mem:['江城','小落','赤辰','绝意','早点'],mh:['伽罗','曜','沈梦溪','鲁班七号','牛魔'],ho:['KPL知名战队','2022年夏季赛季军'],wr:0.58,t:95,de:'新老交替中的KPL战队。'},
{id:'T007',n:'济南RW侠',sh:'RW',r:'济南',c:'行天',ca:'花云',mem:['花云','晨风','无双','千世','夏竹'],mh:['狄仁杰','铠','王昭君','吕布','庄周'],ho:['KPL知名战队','2021季后赛四强'],wr:0.55,t:100,de:'以团战执行力著称的队伍。'},
{id:'T008',n:'深圳DYG',sh:'DYG',r:'深圳',c:'Awoke',ca:'小义',mem:['小义','钎城','决明','湘军','三岁'],mh:['裴擒虎','孙尚香','西施','关羽','盾山'],ho:['KPL知名战队','2020年秋季赛冠军'],wr:0.57,t:105,de:'曾经冠军队伍，正在重建期。'},
{id:'T009',n:'南京Hero久竞',sh:'Hero',r:'南京',c:'尘夏',ca:'无畏',mem:['无畏','傲寒','阿梦','誓约','久凡'],mh:['橘右京','虞姬','诸葛亮','廉颇','太乙真人'],ho:['KPL老牌战队','多次冠军'],wr:0.62,t:118,de:'KPL老牌冠军战队。'},
{id:'T010',n:'苏州KSG',sh:'KSG',r:'苏州',c:'点点',ca:'啊泽',mem:['啊泽','今屿','一曲','小玖','小A'],mh:['花木兰','露娜','奕星','黄忠','孙膑'],ho:['KPL知名战队','2023年夏季赛季军'],wr:0.63,t:102,de:'KPL强队，运营和团战均衡。'},
{id:'T011',n:'长沙TES',sh:'TES',r:'长沙',c:'迷风花',ca:'以然',mem:['以然','蓝桉','暮色','靖风','冰尘'],mh:['镜','马可波罗','嬴政','吕布','张飞'],ho:['KPL知名战队','2022春季赛六强'],wr:0.52,t:98,de:'进攻风格鲜明的KPL战队。'},
{id:'T012',n:'西安WE',sh:'WE',r:'西安',c:'冲天',ca:'佩恩',mem:['佩恩','千世','玄影','安格','748'],mh:['李元芳','赵云','上官婉儿','吕布','蔡文姬'],ho:['KPL老牌战队','多次季后赛'],wr:0.47,t:88,de:'老牌战队，历史战绩辉煌。'},
{id:'T013',n:'厦门VG',sh:'VG',r:'厦门',c:'刘欢',ca:'丶月',mem:['丶月','季节','秀秀','末将','十四'],mh:['沈梦溪','镜','马超','狄仁杰','牛魔'],ho:['KPL战队'],wr:0.42,t:80,de:'KPL中游战队，持续进步中。'},
{id:'T014',n:'上海EDGM',sh:'EDGM',r:'上海',c:'折晨',ca:'柠栀',mem:['柠栀','玄暗','小蝌蚪','初晨','言梦'],mh:['关羽','澜','王昭君','孙尚香','大乔'],ho:['KPL知名战队'],wr:0.45,t:92,de:'上海老牌KPL战队。'},
{id:'T015',n:'广州TTG',sh:'TTG',r:'广州',c:'LoveCD',ca:'九尾',mem:['九尾','不然','清清','帆帆','风箫'],mh:['貂蝉','曜','关羽','虞姬','鲁班大师'],ho:['KPL强队','2023年夏季赛冠军'],wr:0.67,t:125,de:'冠军状态火热，执行力极强。'},
];
const playerDefs = [
{id:'Fly',nk:'Fly',m:20,w:15},{id:'小胖',nk:'小胖',m:18,w:12},{id:'向鱼',nk:'向鱼',m:16,w:11},{id:'妖刀',nk:'妖刀',m:19,w:14},{id:'一笙',nk:'一笙',m:15,w:9},
{id:'一诺',nk:'一诺',m:22,w:16},{id:'长生',nk:'长生',m:17,w:12},{id:'轩染',nk:'轩染',m:14,w:8},{id:'钟意',nk:'钟意',m:18,w:13},{id:'Cat',nk:'Cat',m:21,w:15},
{id:'花海',nk:'花海',m:24,w:18},{id:'清融',nk:'清融',m:20,w:14},{id:'坦然',nk:'坦然',m:17,w:11},{id:'易峥',nk:'易峥',m:18,w:10},{id:'子阳',nk:'子阳',m:16,w:9},
{id:'暖阳',nk:'暖阳',m:22,w:15},{id:'梓墨',nk:'梓墨',m:16,w:10},{id:'花卷',nk:'花卷',m:18,w:11},{id:'乔兮',nk:'乔兮',m:15,w:9},{id:'星宇',nk:'星宇',m:14,w:8},
{id:'青枫',nk:'青枫',m:19,w:11},{id:'鹏鹏',nk:'鹏鹏',m:17,w:10},{id:'百兽',nk:'百兽',m:15,w:8},{id:'梦岚',nk:'梦岚',m:20,w:12},{id:'阿豆',nk:'阿豆',m:13,w:7},
{id:'江城',nk:'江城',m:18,w:10},{id:'小落',nk:'小落',m:14,w:8},{id:'赤辰',nk:'赤辰',m:12,w:6},{id:'绝意',nk:'绝意',m:16,w:9},{id:'早点',nk:'早点',m:11,w:5},
{id:'花云',nk:'花云',m:17,w:9},{id:'晨风',nk:'晨风',m:14,w:7},{id:'无双',nk:'无双',m:13,w:7},{id:'千世',nk:'千世',m:15,w:8},{id:'夏竹',nk:'夏竹',m:11,w:5},
{id:'小义',nk:'小义',m:19,w:11},{id:'钎城',nk:'钎城',m:17,w:9},{id:'决明',nk:'决明',m:13,w:7},{id:'湘军',nk:'湘军',m:12,w:6},{id:'三岁',nk:'三岁',m:10,w:5},
{id:'无畏',nk:'无畏',m:21,w:13},{id:'傲寒',nk:'傲寒',m:16,w:10},{id:'阿梦',nk:'阿梦',m:14,w:8},{id:'誓约',nk:'誓约',m:12,w:7},{id:'久凡',nk:'久凡',m:11,w:6},
{id:'啊泽',nk:'啊泽',m:16,w:10},{id:'今屿',nk:'今屿',m:18,w:11},{id:'一曲',nk:'一曲',m:14,w:8},{id:'小玖',nk:'小玖',m:15,w:9},{id:'小A',nk:'小A',m:12,w:7},
{id:'以然',nk:'以然',m:15,w:8},{id:'蓝桉',nk:'蓝桉',m:13,w:7},{id:'暮色',nk:'暮色',m:11,w:5},{id:'靖风',nk:'靖风',m:10,w:4},{id:'冰尘',nk:'冰尘',m:12,w:6},
{id:'佩恩',nk:'佩恩',m:14,w:6},{id:'玄影',nk:'玄影',m:12,w:5},{id:'安格',nk:'安格',m:10,w:4},{id:'748',nk:'748',m:9,w:3},{id:'川夏',nk:'川夏',m:8,w:3},
{id:'丶月',nk:'丶月',m:12,w:5},{id:'季节',nk:'季节',m:10,w:4},{id:'秀秀',nk:'秀秀',m:9,w:3},{id:'末将',nk:'末将',m:8,w:3},{id:'十四',nk:'十四',m:7,w:2},
{id:'柠栀',nk:'柠栀',m:14,w:6},{id:'玄暗',nk:'玄暗',m:11,w:5},{id:'初晨',nk:'初晨',m:13,w:6},{id:'言梦',nk:'言梦',m:9,w:4},{id:'小蝌蚪',nk:'小蝌蚪',m:8,w:3},
{id:'九尾',nk:'九尾',m:20,w:13},{id:'不然',nk:'不然',m:18,w:12},{id:'清清',nk:'清清',m:16,w:11},{id:'帆帆',nk:'帆帆',m:15,w:10},{id:'风箫',nk:'风箫',m:14,w:9},
];

// ===== GENERATION =====
const allEquips = equipDefs.concat(moreEquips);
function q(s) { return '"' + s.replace(/"/g,'\\"') + '"'; }
function ja(arr) { return 'Arrays.asList(' + arr.map(x=>q(x)).join(',') + ')'; }

let code = 'package util;\n\nimport model.Equipment;\nimport model.Hero;\nimport model.MatchParticipant;\nimport model.MatchRecord;\nimport model.Player;\nimport model.Team;\n\nimport java.time.LocalDateTime;\nimport java.util.ArrayList;\nimport java.util.Arrays;\nimport java.util.List;\n\npublic class DataInitializer {\n    private DataInitializer() {}\n\n';
code += '    public static List<Equipment> initializeEquipments() {\n        List<Equipment> equipments = new ArrayList<>();\n';
for (const e of allEquips) code +=         equipments.add(new Equipment(,,,,,,,,,,,,,,,));\n;
code += '        return equipments;\n    }\n\n';
code += '    public static List<Hero> initializeHeroes() {\n        List<Hero> heroes = new ArrayList<>();\n';
for (const h of heroDefs.concat(heroDefs2)) code +=         heroes.add(new Hero(,,,,,,,,,,,,,,,,));\n;
code += '        return heroes;\n    }\n\n';
code += '    public static List<Team> initializeTeams() {\n        List<Team> teams = new ArrayList<>();\n';
for (const t of teamDefs) code +=         teams.add(new Team(,,,,,,,,,,,,LocalDateTime.now(),"正常"));\n;
code += '        return teams;\n    }\n\n';
code += '    public static List<Player> initializePlayers() {\n        List<Player> players = new ArrayList<>();\n';
for (const p of playerDefs) code +=         players.add(createPlayer(,,,));\n;
code += '        return players;\n    }\n\n';
code += '    private static Player createPlayer(String id,String nickname,int totalMatches,int wins) {\n';
code += '        Player player = new Player(id,id,"",nickname);\n        player.setNickname(nickname);\n';
code += '        List<MatchRecord> records = new ArrayList<>();\n        for(int i=0;i<totalMatches;i++) {\n';
code += '            String result = i<wins?"胜利":"失败";\n            records.add(buildMatchRecord(id,nickname,i+1,result));\n        }\n';
code += '        player.setMatchOverviews(records);\n        return player;\n    }\n\n';
code += '    private static MatchRecord buildMatchRecord(String playerId,String playerName,int index,String result) {\n';
code += '        String matchId = "M"+String.format("%03d",index);\n        LocalDateTime matchTime = LocalDateTime.now().minusDays(index*3L);\n        int durationSeconds = 900+(index%9)*100;\n        List<MatchParticipant> participants = new ArrayList<>();\n';
code += '        participants.add(new MatchParticipant(playerId,playerName,"亚瑟","蓝方",5+index%8,2+index%5,6+index%7));\n';
code += '        participants.add(new MatchParticipant("OPP"+index,"对手"+index,"后羿","红方",2+index%4,4+index%6,3+index%5));\n';
code += '        return new MatchRecord(matchId,matchTime,"排位赛",result,durationSeconds,participants);\n    }\n}\n';

fs.writeFileSync(path, code, 'utf8');
const s = fs.statSync(path);
console.log(Done! Heroes: Equip: Teams: Players:);
console.log(File: KB lines);
