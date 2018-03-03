$(function() {

		var mychart1 = echarts.init(document.getElementById('echart1'));
		var mychart2 = echarts.init(document.getElementById('echart2'));
		var mychart3 = echarts.init(document.getElementById('echart3'));
		var mychart4 = echarts.init(document.getElementById('echart4'));
		var option1 = {
					    title: {
					        text: '业务运行情况',
					        show:false
					    },
					    tooltip: {
					        trigger: 'axis'
					    },
					    legend: {
					        data:['发送条数']
					    },
					    toolbox: {
					        show: true,
					        feature: {
					            dataZoom: {
					                yAxisIndex: 'none'
					            },
					            dataView: {readOnly: false},
					            magicType: {type: ['line', 'bar']},
					            restore: {},
					            saveAsImage: {}
					        }
					    },
					    xAxis:  {
					        type: 'category',
					        boundaryGap: false,
					        data: ['2016-07-16','2016-07-20','2016-07-24','2016-07-28','2016-08-01','2016-08-05','2016-08-09','2016-08-13']
					    },
					    yAxis: {
					        type: 'value',
					        axisLabel: {
					            formatter: '{value}'
					        }
					    },
					    series: [
					        {
					            name:'发送条数',
					            type:'line',
					            data:[0, 0, 0, 0, 1, 1.5, 2, 1],
					            markLine : {
					                data : [
					                    {type : 'average', name: '平均值'}
					                ]
					            }
					        },
					    ]
					};

		var	option2 = {
			    tooltip : {
			        trigger: 'axis'
			    },
			    legend: {
			        data:['发送成功','发送失败','未知']
			    },
			    toolbox: {
			        show : true,
			        feature : {
			            mark : {show: true},
			            dataView : {show: true, readOnly: false},
			            magicType : {show: true, type: ['line', 'bar', 'stack', 'tiled']},
			            restore : {show: true},
			            saveAsImage : {show: true}
			        }
			    },
			    calculable : true,
			    xAxis : [
			        {
			            type : 'category',
			            boundaryGap : false,
			            data : ['01月','02月','03月','04月','05月','06月','07月','08月']
			        }
			    ],
			    yAxis : [
			        {
			            type : 'value'
			        }
			    ],
			    series : [
			        {
			            name:'未知',
			            type:'line',
			            smooth:true,
			            itemStyle: {normal: {areaStyle: {type: 'default'}}},
			            data:[0, 0, 0, 1, 0,0, 0,0]
			        },
			        {
			            name:'发送失败',
			            type:'line',
			            smooth:true,
			            itemStyle: {normal: {areaStyle: {type: 'default'}}},
			            data:[0, 0, 0, 0, 0, 0, 1,3]
			        },
			        {
			            name:'发送成功',
			            type:'line',
			            smooth:true,
			            itemStyle: {normal: {areaStyle: {type: 'default'}}},
			            data:[1, 3, 0.5, 1, 2, 3, 2,0]
			        }
			    ]
			};
                    

		var	option3 = {
					    tooltip : {
					        trigger: 'item',
					        formatter: "{a} <br/>{b} : {c} ({d}%)"
					    },
					    legend: {
					        orient: 'vertical',
					        left: 'left',
					        data: ['错误']
					    },
					    series : [
					        {
					            name: '错误',
					            type: 'pie',
					            radius : '100%',
					            center: ['50%', '50%'],
					            data:[
					                {value:1548, name:'错误'}
					            ],
					            itemStyle: {
					                emphasis: {
					                    shadowBlur: 10,
					                    shadowOffsetX: 0,
					                    shadowColor: 'rgba(0, 0, 0, 0.5)'
					                }
					            }
					        }
					    ]
					};


		var	option4 = {
			    tooltip : {
			        trigger: 'axis'
			    },
			    legend: {
			        data:['发送速度']
			    },
			    toolbox: {
			        show : true,
			        feature : {
			            mark : {show: true},
			            dataView : {show: true, readOnly: false},
			            magicType : {show: true, type: ['line', 'bar']},
			            restore : {show: true},
			            saveAsImage : {show: true}
			        }
			    },
			    calculable : true,
			    xAxis : [
			        {
			            type : 'category',
			            data : ['0-5/s','5-10/s','10-15/s','15-30/s','30-60/s','60s以上']
			        }
			    ],
			    yAxis : [
			        {
			            type : 'value'
			        }
			    ],
			    series : [
			        {
			            name:'发送速度',
			            type:'bar',
			            data:[2.0, 60.9, 7.0, 23.2, 25.6, 30.0],
			            markPoint : {
			                data : [
			                    {type : 'max', name: '最大值'},
			                    {type : 'min', name: '最小值'}
			                ]
			            },
			            markLine : {
			                data : [
			                    {type : 'average', name: '平均值'}
			                ]
			            }
			        },
			    ]
			};
                    

















		mychart1.setOption(option1);
		mychart2.setOption(option2);
		mychart3.setOption(option3);
		mychart4.setOption(option4);
})
