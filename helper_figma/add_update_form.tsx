import { useState } from 'react';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from './ui/card';
import { Input } from './ui/input';
import { Label } from './ui/label';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from './ui/select';
import { Switch } from './ui/switch';
import { RadioGroup, RadioGroupItem } from './ui/radio-group';
import { Button } from './ui/button';
import { toast } from 'sonner@2.0.3';
import { ScanLine, Search } from 'lucide-react';

interface Resource {
  id: string;
  name: string;
  type: 'object' | 'container';
  isBaseContainer: boolean;
  resourceType: 'virtual' | 'physical';
  location: string;
  parentContainer: string;
}

interface ResourceFormProps {
  existingResources: Resource[];
}

interface FormData {
  name: string;
  type: 'object' | 'container' | '';
  isBaseContainer: boolean;
  resourceType: 'virtual' | 'physical' | '';
  location: string;
  parentContainer: string;
  resourceId: string;
}

export function ResourceForm({ existingResources }: ResourceFormProps) {
  const [formData, setFormData] = useState<FormData>({
    name: '',
    type: '',
    isBaseContainer: false,
    resourceType: '',
    location: '',
    parentContainer: '',
    resourceId: '',
  });

  const [isScanning, setIsScanning] = useState(false);
  const [isExistingResource, setIsExistingResource] = useState(false);

  const handleResourceIdChange = (value: string) => {
    setFormData({ ...formData, resourceId: value });
    
    // Look up the resource
    const foundResource = existingResources.find((r) => r.id === value);
    
    if (foundResource) {
      // Auto-fill the form with existing resource data
      setFormData({
        resourceId: value,
        name: foundResource.name,
        type: foundResource.type,
        isBaseContainer: foundResource.isBaseContainer,
        resourceType: foundResource.resourceType,
        location: foundResource.location,
        parentContainer: foundResource.parentContainer,
      });
      setIsExistingResource(true);
      toast.success(`Resource found: ${foundResource.name}`);
    } else {
      // Reset other fields for new resource
      setFormData({
        resourceId: value,
        name: '',
        type: '',
        isBaseContainer: false,
        resourceType: '',
        location: '',
        parentContainer: '',
      });
      setIsExistingResource(false);
      if (value) {
        toast.info('New resource - please fill in the details');
      }
    }
  };

  const handleScanBarcode = () => {
    setIsScanning(true);
    // Simulate barcode scan - in production, this would use a camera or barcode scanner device
    setTimeout(() => {
      const mockBarcode = `RES${Math.floor(Math.random() * 10000).toString().padStart(4, '0')}`;
      handleResourceIdChange(mockBarcode);
      setIsScanning(false);
      toast.success(`Barcode scanned: ${mockBarcode}`);
    }, 1500);
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    
    // Validation
    if (!formData.resourceId) {
      toast.error('Resource ID is required');
      return;
    }

    if (!formData.name || !formData.type || !formData.resourceType) {
      toast.error('Please fill in all required fields');
      return;
    }

    if (formData.type === 'container' && formData.isBaseContainer && !formData.location) {
      toast.error('Location is required for base containers');
      return;
    }

    if (formData.type === 'container' && !formData.isBaseContainer && !formData.parentContainer) {
      toast.error('Parent container is required for non-base containers');
      return;
    }

    toast.success(isExistingResource ? 'Resource updated successfully' : 'Resource created successfully');
    console.log('Form submitted:', formData);
  };

  const handleReset = () => {
    setFormData({
      name: '',
      type: '',
      isBaseContainer: false,
      resourceType: '',
      location: '',
      parentContainer: '',
      resourceId: '',
    });
    setIsExistingResource(false);
    toast.info('Form reset');
  };

  return (
    <Card>
      <CardHeader>
        <CardTitle>Resource Management</CardTitle>
        <CardDescription>
          Scan or enter a Resource ID to lookup or create a resource
        </CardDescription>
      </CardHeader>
      <CardContent>
        <form onSubmit={handleSubmit} className="space-y-6">
          {/* Resource ID (Barcode Scanner) - First Field */}
          <div className="space-y-2">
            <Label htmlFor="resourceId">Resource ID (Barcode) *</Label>
            <div className="flex gap-2">
              <Input
                id="resourceId"
                value={formData.resourceId}
                onChange={(e) => handleResourceIdChange(e.target.value)}
                placeholder="Scan barcode or enter manually"
                required
              />
              <Button
                type="button"
                variant="outline"
                onClick={handleScanBarcode}
                disabled={isScanning}
              >
                <ScanLine className="mr-2 h-4 w-4" />
                {isScanning ? 'Scanning...' : 'Scan'}
              </Button>
            </div>
            <p className="text-sm text-gray-500">
              Enter a Resource ID to lookup existing resource or create a new one. Try: RES001, RES002, RES003, RES004
            </p>
          </div>

          {/* Status Badge */}
          {formData.resourceId && (
            <div className="flex items-center gap-2 p-3 bg-blue-50 border border-blue-200 rounded-md">
              <Search className="h-4 w-4 text-blue-600" />
              <span className="text-sm text-blue-900">
                {isExistingResource 
                  ? `Editing existing resource: ${formData.resourceId}` 
                  : `Creating new resource: ${formData.resourceId}`}
              </span>
            </div>
          )}

          {/* Name */}
          <div className="space-y-2">
            <Label htmlFor="name">Name *</Label>
            <Input
              id="name"
              value={formData.name}
              onChange={(e) => setFormData({ ...formData, name: e.target.value })}
              placeholder="Enter resource name"
              required
            />
          </div>

          {/* Type */}
          <div className="space-y-2">
            <Label htmlFor="type">Type *</Label>
            <Select
              value={formData.type}
              onValueChange={(value: 'object' | 'container') =>
                setFormData({ ...formData, type: value })
              }
            >
              <SelectTrigger id="type">
                <SelectValue placeholder="Select type" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="object">Object</SelectItem>
                <SelectItem value="container">Container</SelectItem>
              </SelectContent>
            </Select>
          </div>

          {/* Is Base Container - only show if type is container */}
          {formData.type === 'container' && (
            <div className="flex items-center space-x-2">
              <Switch
                id="isBaseContainer"
                checked={formData.isBaseContainer}
                onCheckedChange={(checked) =>
                  setFormData({ ...formData, isBaseContainer: checked })
                }
              />
              <Label htmlFor="isBaseContainer" className="cursor-pointer">
                Is this a base container? (Main holder)
              </Label>
            </div>
          )}

          {/* Resource Type */}
          <div className="space-y-2">
            <Label>Resource Type *</Label>
            <RadioGroup
              value={formData.resourceType}
              onValueChange={(value: 'virtual' | 'physical') =>
                setFormData({ ...formData, resourceType: value })
              }
            >
              <div className="flex items-center space-x-2">
                <RadioGroupItem value="virtual" id="virtual" />
                <Label htmlFor="virtual" className="cursor-pointer">
                  Virtual
                </Label>
              </div>
              <div className="flex items-center space-x-2">
                <RadioGroupItem value="physical" id="physical" />
                <Label htmlFor="physical" className="cursor-pointer">
                  Physical
                </Label>
              </div>
            </RadioGroup>
          </div>

{/* Conditional: Location or Parent Container */}
{formData.type === 'container' && formData.isBaseContainer ? (
  <div className="space-y-2">
    <Label htmlFor="location">Location *</Label>
    <Input
      id="location"
      value={formData.location}
      onChange={(e) => setFormData({ ...formData, location: e.target.value })}
      placeholder="Enter location"
      required
    />
  </div>
) : (formData.type === 'object' || (formData.type === 'container' && !formData.isBaseContainer)) ? (
  <div className="space-y-2">
    <Label htmlFor="parentContainer">Parent Container *</Label>
    <Select
      value={formData.parentContainer}
      onValueChange={(value) =>
        setFormData({ ...formData, parentContainer: value })
      }
    >
      <SelectTrigger id="parentContainer">
        <SelectValue placeholder="Select parent container" />
      </SelectTrigger>
      <SelectContent>
        {existingResources
          .filter((r) => r.type === 'container')
          .map((resource) => (
            <SelectItem key={resource.id} value={resource.id}>
              {resource.id} - {resource.name}
            </SelectItem>
          ))}
      </SelectContent>
    </Select>
  </div>
) : null}
          {/* Submit Button */}
          <div className="flex gap-2 pt-4">
            <Button type="submit" className="flex-1">
              {isExistingResource ? 'Update Resource' : 'Create Resource'}
            </Button>
            <Button
              type="button"
              variant="outline"
              onClick={handleReset}
            >
              Reset
            </Button>
          </div>
        </form>
      </CardContent>
    </Card>
  );
}
