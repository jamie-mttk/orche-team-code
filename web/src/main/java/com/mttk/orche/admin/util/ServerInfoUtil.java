package com.mttk.orche.admin.util;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;

import org.bson.Document;

import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.CentralProcessor.TickType;
import oshi.hardware.ComputerSystem;
import oshi.hardware.Display;
import oshi.hardware.GlobalMemory;
import oshi.hardware.GraphicsCard;
import oshi.hardware.HWDiskStore;
import oshi.hardware.HWPartition;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.LogicalVolumeGroup;
import oshi.hardware.NetworkIF;
import oshi.hardware.PhysicalMemory;
import oshi.hardware.PowerSource;
import oshi.hardware.Sensors;
import oshi.hardware.SoundCard;
import oshi.hardware.UsbDevice;
import oshi.hardware.VirtualMemory;
import oshi.software.os.FileSystem;
import oshi.software.os.InternetProtocolStats;
import oshi.software.os.NetworkParams;
import oshi.software.os.OSFileStore;
import oshi.software.os.OSProcess;
import oshi.software.os.OSService;
import oshi.software.os.OSSession;
import oshi.software.os.OperatingSystem;
import oshi.software.os.OperatingSystem.ProcessFiltering;
import oshi.software.os.OperatingSystem.ProcessSorting;
import oshi.util.FormatUtil;
import oshi.util.Util;

public class ServerInfoUtil {
	//
	public static Document  fillInfo() {
		Document doc=new Document();
		//
	   SystemInfo si = new SystemInfo();
        HardwareAbstractionLayer hal = si.getHardware();
        CentralProcessor processor=hal.getProcessor();
        //CPU
        long[] prevTicks = processor.getSystemCpuLoadTicks();
        // 
        Util.sleep(1000);
        long[] ticks = processor.getSystemCpuLoadTicks();
        long user = ticks[TickType.USER.getIndex()] - prevTicks[TickType.USER.getIndex()];
        long nice = ticks[TickType.NICE.getIndex()] - prevTicks[TickType.NICE.getIndex()];
        long sys = ticks[TickType.SYSTEM.getIndex()] - prevTicks[TickType.SYSTEM.getIndex()];
        long idle = ticks[TickType.IDLE.getIndex()] - prevTicks[TickType.IDLE.getIndex()];
        long iowait = ticks[TickType.IOWAIT.getIndex()] - prevTicks[TickType.IOWAIT.getIndex()];
        long irq = ticks[TickType.IRQ.getIndex()] - prevTicks[TickType.IRQ.getIndex()];
        long softirq = ticks[TickType.SOFTIRQ.getIndex()] - prevTicks[TickType.SOFTIRQ.getIndex()];
        long steal = ticks[TickType.STEAL.getIndex()] - prevTicks[TickType.STEAL.getIndex()];
        long total = user + nice + sys + idle + iowait + irq + softirq + steal;
        doc.append("cpu_user", user);
        doc.append("cpu_nice", nice);
        doc.append("cpu_sys", sys);
        doc.append("cpu_idle", idle);
        doc.append("cpu_iowait", iowait);
        doc.append("cpu_irq", irq);
        doc.append("cpu_softirq", softirq);
        doc.append("cpu_steal", steal);
        doc.append("cpu_total", total);
        //
        //RAM
        GlobalMemory gm=hal.getMemory();
        doc.append("mem_total", gm.getTotal());
        doc.append("mem_available", gm.getAvailable());
        Runtime runtime=Runtime.getRuntime();
        doc.append("mem_vm_total", runtime.totalMemory());
        doc.append("mem_vm_available", runtime.freeMemory());
        doc.append("mem_vm_used", runtime.totalMemory() - runtime.freeMemory());
		//
		return doc;
	}

	//得到完整的机器硬件信息
	 public static String machineInfo() {
	    	StringBuilder sb=new StringBuilder(4096);
	        SystemInfo si = new SystemInfo();

	        HardwareAbstractionLayer hal = si.getHardware();
	        OperatingSystem os = si.getOperatingSystem();
	        sb.append("###Operation System...\n");
	       printOperatingSystem(sb,os);
	        sb.append("\n###computer system...\n");
	        printComputerSystem(sb,hal.getComputerSystem());
	        sb.append("\n###Processor...\n");
	        printProcessor(sb,hal.getProcessor());
	        sb.append("\n###Memory...\n");
	        printMemory(sb,hal.getMemory());
	        sb.append("\n###CPU...\n");
	        printCpu(sb,hal.getProcessor());
	        sb.append("\n###Processes...\n");
	        printProcesses(sb,os, hal.getMemory());
	        sb.append("Services...\n");
	        printServices(sb,os);
	        sb.append("\n###Sensors...\n");
	        printSensors(sb,hal.getSensors());
	        sb.append("\n###Power sources...\n");
	        printPowerSources(sb,hal.getPowerSources());
	        sb.append("\n###Disks...\n");
	        printDisks(sb,hal.getDiskStores());
	        sb.append("Logical Volume Groups ...\n");
	        printLVgroups(sb,hal.getLogicalVolumeGroups());
	        sb.append("\n###File System...\n");
	        printFileSystem(sb,os.getFileSystem());
	        sb.append("\n###Network interfaces...\n");
	        printNetworkInterfaces(sb,hal.getNetworkIFs());
	        sb.append("\n###Network parameterss...\n");
	        printNetworkParameters(sb,os.getNetworkParams());
	        sb.append("IP statistics...\n");
	        printInternetProtocolStats(sb,os.getInternetProtocolStats());
	        // hardware: displays
	        sb.append("\n###Displays...\n");
	        printDisplays(sb,hal.getDisplays());
	        // hardware: USB devices
	        sb.append("\n###USB Devices...\n");
	        printUsbDevices(sb,hal.getUsbDevices(true));
	        sb.append("Sound Cards...\n");
	        printSoundCards(sb,hal.getSoundCards());

	        sb.append("Graphics Cards...\n");
	        printGraphicsCards(sb,hal.getGraphicsCards());
	        //
	        return sb.toString();
	    }
	
	 
	 private static void printOperatingSystem(StringBuilder sb,final OperatingSystem os) {
	        appendLine(sb,String.valueOf(os));
	        appendLine(sb,"Booted: " + Instant.ofEpochSecond(os.getSystemBootTime()));
	        appendLine(sb,"Uptime: " + FormatUtil.formatElapsedSecs(os.getSystemUptime()));
	        appendLine(sb,"Running with" + (os.isElevated() ? "" : "out") + " elevated permissions.");
	        appendLine(sb,"Sessions:");
	        for (OSSession s : os.getSessions()) {
	            appendLine(sb," " + s.toString());
	        }
	    }

	    private static void printComputerSystem(StringBuilder sb,final ComputerSystem computerSystem) {
	        appendLine(sb,"System: " + computerSystem.toString());
	        appendLine(sb," Firmware: " + computerSystem.getFirmware().toString());
	        appendLine(sb," Baseboard: " + computerSystem.getBaseboard().toString());
	    }

	    private static void printProcessor(StringBuilder sb,CentralProcessor processor) {
	        appendLine(sb,processor.toString());
	    }

	    private static void printMemory(StringBuilder sb,GlobalMemory memory) {
	        appendLine(sb,"Physical Memory: \n " + memory.toString());
	        VirtualMemory vm = memory.getVirtualMemory();
	        appendLine(sb,"Virtual Memory: \n " + vm.toString());
	        List<PhysicalMemory> pmList = memory.getPhysicalMemory();
	        if (!pmList.isEmpty()) {
	            appendLine(sb,"Physical Memory: ");
	            for (PhysicalMemory pm : pmList) {
	                appendLine(sb," " + pm.toString());
	            }
	        }
	    }

	    private static void printCpu(StringBuilder sb,CentralProcessor processor) {
	        appendLine(sb,"Context Switches/Interrupts: " + processor.getContextSwitches() + " / " + processor.getInterrupts());

	        long[] prevTicks = processor.getSystemCpuLoadTicks();
	        long[][] prevProcTicks = processor.getProcessorCpuLoadTicks();
	        appendLine(sb,"CPU, IOWait, and IRQ ticks @ 0 sec:" + Arrays.toString(prevTicks));
	        // Wait a second...
	        Util.sleep(1000);
	        long[] ticks = processor.getSystemCpuLoadTicks();
	        appendLine(sb,"CPU, IOWait, and IRQ ticks @ 1 sec:" + Arrays.toString(ticks));
	        long user = ticks[TickType.USER.getIndex()] - prevTicks[TickType.USER.getIndex()];
	        long nice = ticks[TickType.NICE.getIndex()] - prevTicks[TickType.NICE.getIndex()];
	        long sys = ticks[TickType.SYSTEM.getIndex()] - prevTicks[TickType.SYSTEM.getIndex()];
	        long idle = ticks[TickType.IDLE.getIndex()] - prevTicks[TickType.IDLE.getIndex()];
	        long iowait = ticks[TickType.IOWAIT.getIndex()] - prevTicks[TickType.IOWAIT.getIndex()];
	        long irq = ticks[TickType.IRQ.getIndex()] - prevTicks[TickType.IRQ.getIndex()];
	        long softirq = ticks[TickType.SOFTIRQ.getIndex()] - prevTicks[TickType.SOFTIRQ.getIndex()];
	        long steal = ticks[TickType.STEAL.getIndex()] - prevTicks[TickType.STEAL.getIndex()];
	        long totalCpu = user + nice + sys + idle + iowait + irq + softirq + steal;

	        appendLine(sb,String.format(
	                "User: %.1f%% Nice: %.1f%% System: %.1f%% Idle: %.1f%% IOwait: %.1f%% IRQ: %.1f%% SoftIRQ: %.1f%% Steal: %.1f%%",
	                100d * user / totalCpu, 100d * nice / totalCpu, 100d * sys / totalCpu, 100d * idle / totalCpu,
	                100d * iowait / totalCpu, 100d * irq / totalCpu, 100d * softirq / totalCpu, 100d * steal / totalCpu));
	        appendLine(sb,String.format("CPU load: %.1f%%", processor.getSystemCpuLoadBetweenTicks(prevTicks) * 100));
	        double[] loadAverage = processor.getSystemLoadAverage(3);
	        appendLine(sb,"CPU load averages:" + (loadAverage[0] < 0 ? " N/A" : String.format(" %.2f", loadAverage[0]))
	                + (loadAverage[1] < 0 ? " N/A" : String.format(" %.2f", loadAverage[1]))
	                + (loadAverage[2] < 0 ? " N/A" : String.format(" %.2f", loadAverage[2])));
	        // per core CPU
	        StringBuilder procCpu = new StringBuilder("CPU load per processor:");
	        double[] load = processor.getProcessorCpuLoadBetweenTicks(prevProcTicks);
	        for (double avg : load) {
	            procCpu.append(String.format(" %.1f%%", avg * 100));
	        }

	        appendLine(sb,procCpu.toString());
	        long freq = processor.getProcessorIdentifier().getVendorFreq();
	        if (freq > 0) {
	            appendLine(sb,"Vendor Frequency: " + FormatUtil.formatHertz(freq));
	        }
	        freq = processor.getMaxFreq();
	        if (freq > 0) {
	            appendLine(sb,"Max Frequency: " + FormatUtil.formatHertz(freq));
	        }
	        long[] freqs = processor.getCurrentFreq();
	        if (freqs[0] > 0) {
	            StringBuilder sb1 = new StringBuilder("Current Frequencies: ");
	            for (int i = 0; i < freqs.length; i++) {
	                if (i > 0) {
	                    sb1.append(", ");
	                }
	                sb1.append(FormatUtil.formatHertz(freqs[i]));
	            }
	            appendLine(sb,sb1.toString());
	        }
	    }

	    private static void printProcesses(StringBuilder sb,OperatingSystem os, GlobalMemory memory) {
	        OSProcess myProc = os.getProcess(os.getProcessId());
	        // current process will never be null. Other code should check for null here
	        appendLine(sb,
	                "My PID: " + myProc.getProcessID() + " with affinity " + Long.toBinaryString(myProc.getAffinityMask()));
	        appendLine(sb,"Processes: " + os.getProcessCount() + ", Threads: " + os.getThreadCount());
	        // Sort by highest CPU
	        List<OSProcess> procs = os.getProcesses(ProcessFiltering.ALL_PROCESSES, ProcessSorting.CPU_DESC, 5);
	        appendLine(sb,"   PID  %CPU %MEM       VSZ       RSS Name");
	        for (int i = 0; i < procs.size() && i < 5; i++) {
	            OSProcess p = procs.get(i);
	            appendLine(sb,String.format(" %5d %5.1f %4.1f %9s %9s %s", p.getProcessID(),
	                    100d * (p.getKernelTime() + p.getUserTime()) / p.getUpTime(),
	                    100d * p.getResidentSetSize() / memory.getTotal(), FormatUtil.formatBytes(p.getVirtualSize()),
	                    FormatUtil.formatBytes(p.getResidentSetSize()), p.getName()));
	        }
	        OSProcess p = os.getProcess(os.getProcessId());
	        appendLine(sb,"Current process arguments: ");
	        for (String s : p.getArguments()) {
	            appendLine(sb,"  " + s);
	        }
	        appendLine(sb,"Current process environment: ");
	        for (Entry<String, String> e : p.getEnvironmentVariables().entrySet()) {
	            appendLine(sb,"  " + e.getKey() + "=" + e.getValue());
	        }
	    }

	    private static void printServices(StringBuilder sb,OperatingSystem os) {
	        appendLine(sb,"Services: ");
	        appendLine(sb,"   PID   State   Name");
	        // DO 5 each of running and stopped
	        int i = 0;
	        for (OSService s : os.getServices()) {
	            if (s.getState().equals(OSService.State.RUNNING) && i++ < 5) {
	                appendLine(sb,String.format(" %5d  %7s  %s", s.getProcessID(), s.getState(), s.getName()));
	            }
	        }
	        i = 0;
	        for (OSService s : os.getServices()) {
	            if (s.getState().equals(OSService.State.STOPPED) && i++ < 5) {
	                appendLine(sb,String.format(" %5d  %7s  %s", s.getProcessID(), s.getState(), s.getName()));
	            }
	        }
	    }

	    private static void printSensors(StringBuilder sb,Sensors sensors) {
	        appendLine(sb,"Sensors: " + sensors.toString());
	    }

	    private static void printPowerSources(StringBuilder sb,List<PowerSource> list) {
	        StringBuilder sb1 = new StringBuilder("Power Sources: ");
	        if (list.isEmpty()) {
	            sb1.append("Unknown");
	        }
	        for (PowerSource powerSource : list) {
	            sb1.append("\n ").append(powerSource.toString());
	        }
	        appendLine(sb,sb1.toString());
	    }

	    private static void printDisks(StringBuilder sb,List<HWDiskStore> list) {
	        appendLine(sb,"Disks:");
	        for (HWDiskStore disk : list) {
	            appendLine(sb," " + disk.toString());

	            List<HWPartition> partitions = disk.getPartitions();
	            for (HWPartition part : partitions) {
	                appendLine(sb," |-- " + part.toString());
	            }
	        }

	    }

	    private static void printLVgroups(StringBuilder sb,List<LogicalVolumeGroup> list) {
	        if (!list.isEmpty()) {
	            appendLine(sb,"Logical Volume Groups:");
	            for (LogicalVolumeGroup lvg : list) {
	                appendLine(sb," " + lvg.toString());
	            }
	        }
	    }

	    private static void printFileSystem(StringBuilder sb,FileSystem fileSystem) {
	        appendLine(sb,"File System:");

	        appendLine(sb,String.format(" File Descriptors: %d/%d", fileSystem.getOpenFileDescriptors(),
	                fileSystem.getMaxFileDescriptors()));

	        for (OSFileStore fs : fileSystem.getFileStores()) {
	            long usable = fs.getUsableSpace();
	            long total = fs.getTotalSpace();
	            appendLine(sb,String.format(
	                    " %s (%s) [%s] %s of %s free (%.1f%%), %s of %s files free (%.1f%%) is %s "
	                            + (fs.getLogicalVolume() != null && fs.getLogicalVolume().length() > 0 ? "[%s]" : "%s")
	                            + " and is mounted at %s",
	                    fs.getName(), fs.getDescription().isEmpty() ? "file system" : fs.getDescription(), fs.getType(),
	                    FormatUtil.formatBytes(usable), FormatUtil.formatBytes(fs.getTotalSpace()), 100d * usable / total,
	                    FormatUtil.formatValue(fs.getFreeInodes(), ""), FormatUtil.formatValue(fs.getTotalInodes(), ""),
	                    100d * fs.getFreeInodes() / fs.getTotalInodes(), fs.getVolume(), fs.getLogicalVolume(),
	                    fs.getMount()));
	        }
	    }

	    private static void printNetworkInterfaces(StringBuilder sb,List<NetworkIF> list) {
	        StringBuilder sb1 = new StringBuilder("Network Interfaces:");
	        if (list.isEmpty()) {
	            sb1.append(" Unknown");
	        } else {
	            for (NetworkIF net : list) {
	                sb1.append("\n ").append(net.toString());
	            }
	        }
	        appendLine(sb,sb1.toString());
	    }

	    private static void printNetworkParameters(StringBuilder sb,NetworkParams networkParams) {
	        appendLine(sb,"Network parameters:\n " + networkParams.toString());
	    }

	    private static void printInternetProtocolStats(StringBuilder sb,InternetProtocolStats ip) {
	        appendLine(sb,"Internet Protocol statistics:");
	        appendLine(sb," TCPv4: " + ip.getTCPv4Stats());
	        appendLine(sb," TCPv6: " + ip.getTCPv6Stats());
	        appendLine(sb," UDPv4: " + ip.getUDPv4Stats());
	        appendLine(sb," UDPv6: " + ip.getUDPv6Stats());
	    }

	    private static void printDisplays(StringBuilder sb,List<Display> list) {
	        appendLine(sb,"Displays:");
	        int i = 0;
	        for (Display display : list) {
	            appendLine(sb," Display " + i + ":");
	            appendLine(sb,String.valueOf(display));
	            i++;
	        }
	    }

	    private static void printUsbDevices(StringBuilder sb,List<UsbDevice> list) {
	        appendLine(sb,"USB Devices:");
	        for (UsbDevice usbDevice : list) {
	            appendLine(sb,String.valueOf(usbDevice));
	        }
	    }

	    private static void printSoundCards(StringBuilder sb,List<SoundCard> list) {
	        appendLine(sb,"Sound Cards:");
	        for (SoundCard card : list) {
	            appendLine(sb," " + String.valueOf(card));
	        }
	    }

	    private static void printGraphicsCards(StringBuilder sb,List<GraphicsCard> list) {
	        appendLine(sb,"Graphics Cards:");
	        if (list.isEmpty()) {
	            appendLine(sb," None detected.");
	        } else {
	            for (GraphicsCard card : list) {
	                appendLine(sb," " + String.valueOf(card));
	            }
	        }
	    }
	    //
	    private static void appendLine(StringBuilder sb,String line) {
	    	sb.append(line).append("\n");
	    }
}
